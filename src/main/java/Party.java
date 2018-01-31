import ecdsa.GexECDSA;
import udp.FragmentPacket;
import udp.GexMessage;
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
    private Map<Integer, ArrayList<FragmentPacket>> received;

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
