import udp.MessagePacket;
import udp.UDPClient;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Party {

    private UDPClient udpClient;
    private Map<String, ArrayList<MessagePacket>> received;

    // Todo: party.yml constructor?

    public Party(String addr, int port) throws SocketException, UnknownHostException {
        udpClient = new UDPClient(addr, port);
        received = new HashMap<String, ArrayList<MessagePacket>>();
        // TODO ECDSA keyPair ?
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

            if (!received.containsKey(mp.getNonce())) {
//                System.out.println("Creating new key: " + mp.getNonce());
                received.put(mp.getNonce(), new ArrayList<MessagePacket>());
            }

            received.get(mp.getNonce()).add(mp);

            processMessage(mp);
        }

    }

    public void processMessage(MessagePacket mp) throws Exception {
        if (mp.getIndex() + 1 == mp.getAmount()) {


//            MessagePacket[] packets = (MessagePacket[]) received.get(mp.getNonce()).toArray();
            MessagePacket[] packets = new MessagePacket[received.get(mp.getNonce()).size()];
            received.get(mp.getNonce()).toArray(packets);

            String assembled = MessagePacket.assembleMessage(packets);
            System.out.println("GOT assembled message: " + assembled);
        }
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
