import ecdsa.GexECDSA;
import udp.FragmentPacket;
import udp.GexMessage;
import udp.UDPClient;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Party {

    private static final String PUBLIC_KEY_NAME = "publicKey";
    private static final String PRIVATE_KEY_NAME = "privateKey";

    private UDPClient udpClient;
    private GexECDSA gexECDSA;
    private Map<Integer, ArrayList<FragmentPacket>> received;
    private ArrayList<PublicKey> publicKeys;

    // Todo: party.yml constructor?

    /**
     * Creates new ECDSA keys
     */
    public Party(String addr, int port) throws SocketException, UnknownHostException, NoSuchAlgorithmException {
        udpClient = new UDPClient(addr, port);
        gexECDSA = new GexECDSA();
        received = new HashMap<>();
        publicKeys = new ArrayList<>();
    }


    public Party(String keysDir, String addr, int port) throws IOException,
            InvalidKeySpecException, NoSuchAlgorithmException {

        String privateKeyPath = Paths.get(keysDir, PRIVATE_KEY_NAME).toString();
        String publicKeyPath = Paths.get(keysDir, PUBLIC_KEY_NAME).toString();

        udpClient = new UDPClient(addr, port);
        gexECDSA = new GexECDSA(privateKeyPath, publicKeyPath);
        received = new HashMap<>();
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


    // TODO throws Exception -> change to some customException
    public void sendSignMessage(String msg, String addr, int port) throws IOException, NoSuchAlgorithmException {
        udpClient.sendMessage(msg, "sg", addr, port);
    }

    /**
     * blocking call
     */
    // TODO throws Exception -> change to some customException
    public void receiveMessage() throws Exception {
        while (true) {
            FragmentPacket fp = udpClient.receiveMessage();
//            System.out.println("Received MessagePacket: " + mp);
            int nonceHashCode = fp.getNonceHashCode();

            if (!received.containsKey(nonceHashCode)) {
//                System.out.println("Creating new key: " + mp.getNonce());
                received.put(nonceHashCode, new ArrayList<>());
            }

            received.get(nonceHashCode).add(fp);

            processMessage(fp);
        }

    }

    private void processMessage(FragmentPacket fp) throws Exception {

        // TODO the order could not be guaranteed.. check HashMap[nonce] length or smth...
        if (fp.getIndex() + 1 == fp.getAmount()) {

            int nonceHashCode = fp.getNonceHashCode();
            String command = fp.getCommand();

            FragmentPacket[] packets = new FragmentPacket[received.get(nonceHashCode).size()];

            // ArrayList to Array
            received.get(nonceHashCode).toArray(packets);

            GexMessage assembled = FragmentPacket.assembleMessage(packets);
            System.out.println("GOT assembled message: " + assembled);
        }
    }

//    public void processAssembledMessage(String assembled){
//        switch (assembled.getCommand()){
//            case "sg": String sign = sign(assembled);
//
//
//        }
//    }

    // Todo
    public String sign(String msg) {
        return msg;
    }


//    public static void main(String[] args) {
//        try {
//            Party party = new Party("localhost", 1400);
//        } catch (SocketException | UnknownHostException e) {
//            e.printStackTrace();
//        }
//
//    }
}
