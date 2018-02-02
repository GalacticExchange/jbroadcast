import ecdsa.GexECDSA;
import udp.Communicator;
import udp.GexMessage;
import udp.RandomGenerator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class Party extends Communicator {

    private static final String PUBLIC_KEY_NAME = "publicKey";
    private static final String PRIVATE_KEY_NAME = "privateKey";
    private static final int t = 2;

    //    private UDPClient udpClient;
    private GexECDSA gexECDSA;
    //    private Map<Integer, ArrayList<FragmentPacket>> received;
    private ArrayList<PublicKey> publicKeys;
    private ArrayList<GexMessage> committedMessages;
    // Todo: party.yml constructor?

    /**
     * Creates new ECDSA keys
     */
    public Party(String addr, int port) throws SocketException, UnknownHostException, NoSuchAlgorithmException {
        super(addr, port);
        committedMessages = new ArrayList<>();
        gexECDSA = new GexECDSA();
        publicKeys = new ArrayList<>();
    }

    public Party(String keysDir, String addr, int port) throws IOException,
            InvalidKeySpecException, NoSuchAlgorithmException {
        super(addr, port);

        String privateKeyPath = Paths.get(keysDir, PRIVATE_KEY_NAME).toString();
        String publicKeyPath = Paths.get(keysDir, PUBLIC_KEY_NAME).toString();

        committedMessages = new ArrayList<>();
        gexECDSA = new GexECDSA(privateKeyPath, publicKeyPath);
        publicKeys = new ArrayList<>();
    }


    public void addPublicKeyToList(String publicKeyPath) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        PublicKey key = (PublicKey) GexECDSA.readKey(publicKeyPath, PublicKey.class);
        publicKeys.add(key);
    }

    public void saveKeys(String keysDir) throws IOException {
        String privateKeyPath = Paths.get(keysDir, PRIVATE_KEY_NAME).toString();
        String publicKeyPath = Paths.get(keysDir, PUBLIC_KEY_NAME).toString();
        gexECDSA.saveKeys(privateKeyPath, publicKeyPath);
    }


//    public void sendSignMessage(String msg, String addr, int port) throws IOException, NoSuchAlgorithmException {
//        GexMessage gm = new GexMessage(msg, "sg");
//        sendMessage(gm, addr, port);
//    }


    @Override
    public void processMessage(GexMessage gm, String address, int port) {
        try {

            if (gm.getCommand().equals("sg")) {


                String sig = gexECDSA.sign(gm.getMessage());
                ArrayList<String> arr = new ArrayList<>();
                arr.add(sig);
                GexMessage singedMessage = new GexMessage(gm.getMessage(), "sg", gm.getNonce(), arr);
                sendMessage(singedMessage, address, port);


            } else if (gm.getCommand().equals("ch")) {
                System.out.println("Party got check message:\n" + gm);
                List<String> signs = gm.getSigns();
                int verifiedSigns = 0;

                for (int i = 0; i < signs.size(); i++) {
                    for (int j = 0; j < signs.size(); j++) {
                        if (gexECDSA.verifySign(gm.getMessage(), signs.get(i), publicKeys.get(j))) {
                            verifiedSigns++;

                        }
                    }

                }

                if (verifiedSigns >= (2 * t) + 1) {
                    System.out.println(String.format("Party %s : committing message", this));
                    committedMessages.add(gm);
                }
            }

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | IOException e) {
            e.printStackTrace();
        }
    }


}
