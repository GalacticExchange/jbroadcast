package verifiable;

import ecdsa.GexECDSA;
import udp.Communicator;
import udp.GexMessage;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Party extends Communicator {

    private static final String PUBLIC_KEY_NAME = "publicKey";
    private static final String PRIVATE_KEY_NAME = "privateKey";
    private static final int t = 1;
    public static final int TEST_AMOUNT_MESSAGES = 100;

    //    private UDPClient udpClient;
    private GexECDSA gexECDSA;
    //    private Map<Integer, ArrayList<FragmentPacket>> received;
    private HashMap<String, PublicKey> publicKeys;
    private ArrayList<GexMessage> committedMessages;
    private String name;

    // Todo: party.yml constructor?

    /**
     * Creates new ECDSA keys
     */
    public Party(String addr, int port, String name) throws SocketException, UnknownHostException, NoSuchAlgorithmException {
        super(addr, port);

        this.name = name;
        committedMessages = new ArrayList<>();
        gexECDSA = new GexECDSA();
        publicKeys = new HashMap<>();
    }

    public Party(String keysDir, String addr, int port, String name) throws IOException,
            InvalidKeySpecException, NoSuchAlgorithmException {
        super(addr, port);

        String privateKeyPath = Paths.get(keysDir, PRIVATE_KEY_NAME).toString();
        String publicKeyPath = Paths.get(keysDir, PUBLIC_KEY_NAME).toString();

        this.name = name;

        committedMessages = new ArrayList<>();
        gexECDSA = new GexECDSA(privateKeyPath, publicKeyPath);
        publicKeys = new HashMap<>();
    }


    public void addPublicKeyToList(String name, String publicKeyPath) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        PublicKey key = (PublicKey) GexECDSA.readKey(publicKeyPath, PublicKey.class);
        publicKeys.put(name, key);
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

                HashMap<String, String> map = new HashMap<>();
                map.put(name, sig);

                GexMessage singedMessage = new GexMessage(gm.getMessage(), "sg",
                        gm.getNonce(), gm.getSendTime(), map);
                sendMessage(singedMessage, address, port);


            } else if (gm.getCommand().equals("ch")) {

//                System.out.println("Party got check message:\n" + gm);

                Map<String, String> signs = gm.getSigns();
                int verifiedSigns = 0;

                for (String key : signs.keySet()) {
                    if (gexECDSA.verifySign(gm.getMessage(), signs.get(key), publicKeys.get(key))) {
                        verifiedSigns++;

                        if (verifiedSigns >= (2 * t) + 1){
                            break;
                        }

                    }
                }


//                for (int i = 0; i < signs.size(); i++) {
//                    for (int j = 0; j < signs.size(); j++) {
//                        if (gexECDSA.verifySign(gm.getMessage(), signs.get(i), publicKeys.get(j))) {
//                            verifiedSigns++;
//                        }
//                    }
//
//                }

                if (verifiedSigns >= (2 * t) + 1) {

//                    System.out.println(String.format("Party %s : committing message, time elapsed: s ms",
//                            this));


                    committedMessages.add(gm);

//                    System.out.println(String.format("Party %s : committed messages %s",
//                            this, committedMessages.size()));


                    if (committedMessages.size() == TEST_AMOUNT_MESSAGES) {

                        Instant startTime = Instant.parse(committedMessages.get(0).getSendTime());
                        Instant finishTime = Instant.now();

                        Duration timeElapsed = Duration.between(startTime, finishTime);


                        System.out.println(String.format("Party %s : %s messages elapsed time: %s ", this,
                                TEST_AMOUNT_MESSAGES, timeElapsed));
                    }
                }
            }

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | IOException e) {
            e.printStackTrace();
        }
    }


}
