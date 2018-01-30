import ecdsa.GexECDSA;
import udp.MessagePacket;
import udp.UDPClient;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Party {

    private UDPClient udpClient;
    private GexECDSA gexECDSA;
    private Map<Integer, ArrayList<MessagePacket>> received;

    // Todo: party.yml constructor?

    public Party(String addr, int port) throws SocketException, UnknownHostException, NoSuchAlgorithmException {
        udpClient = new UDPClient(addr, port);
        received = new HashMap<>();
        // TODO ECDSA keyPair ?
        gexECDSA = new GexECDSA();
    }

    public Party(String configPath) {

    }

    private void saveConfig() {

    }


    // TODO throws Exception -> change to some customException
    public void sendSignMessage(String msg, String addr, int port) throws Exception {
        udpClient.sendMessage(msg, "sg", addr, port);
    }

    /**
     * blocking call
     */
    // TODO throws Exception -> change to some customException
    public void receiveMessage() throws Exception {
        while (true) {
            MessagePacket mp = udpClient.receiveMessage();
//            System.out.println("Received MessagePacket: " + mp);
            int nonceHashCode = mp.getNonceHashCode();

            if (!received.containsKey(nonceHashCode)) {
//                System.out.println("Creating new key: " + mp.getNonce());
                received.put(nonceHashCode, new ArrayList<MessagePacket>());
            }

            received.get(nonceHashCode).add(mp);

            processMessage(mp);
        }

    }

    private void processMessage(MessagePacket mp) throws Exception {

        // TODO the order could not be guaranteed.. check HashMap[nonce] length or smth...
        if (mp.getIndex() + 1 == mp.getAmount()) {

            int nonceHashCode = mp.getNonceHashCode();
            String command = mp.getCommand();

            MessagePacket[] packets = new MessagePacket[received.get(nonceHashCode).size()];

            // ArrayList to Array
            received.get(nonceHashCode).toArray(packets);

            String assembled = MessagePacket.assembleMessage(packets);
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
