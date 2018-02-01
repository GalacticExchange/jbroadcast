import udp.Communicator;
import udp.GexMessage;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;

public class Client extends Communicator {

    private Map<Integer, ArrayList<GexMessage>> receivedSignedMessages;

    public Client(String addr, int port) throws SocketException, UnknownHostException {
        super(addr, port);

        new Thread(() -> {
            try {
                receiveMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void sendSignMessage(String msg, ArrayList<Party> parties) throws IOException, NoSuchAlgorithmException {
        GexMessage gm = new GexMessage(msg, "sg");
        for (Party p : parties) {
            System.out.println(p);
            sendMessage(gm, p.getAddress(), p.getPort());
        }
    }

    public void sendCheckMessage(String msg, ArrayList<String> signs, ArrayList<Party> parties) throws IOException,
            NoSuchAlgorithmException {
        GexMessage gm = new GexMessage(msg, "ch", signs);
        for (Party p : parties) {
            sendMessage(gm, p.getAddress(), p.getPort());
        }
    }

    @Override
    public void processMessage(GexMessage gm, String address, int port) {
        System.out.println("------------------------");
        System.out.println(String.format("CLIENT GOT MESSAGE:\n") + gm);
        System.out.println("------------------------");


    }
}
