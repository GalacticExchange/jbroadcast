import udp.Communicator;
import udp.GexMessage;
import udp.RandomGenerator;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Client extends Communicator {

    private Map<String, ArrayList<GexMessage>> receivedSignedMessages;
    private ArrayList<Party> parties;

    public Client(String addr, int port, ArrayList<Party> parties) throws SocketException, UnknownHostException {
        super(addr, port);
        this.parties = parties;
        receivedSignedMessages = new HashMap<>();

        // todo add thread as field?
        new Thread(() -> {
            try {
                receiveMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void sendSignMessage(String msg) throws IOException, NoSuchAlgorithmException {
        String nonce = RandomGenerator.generateString(GexMessage.NONCE_LEN);
        GexMessage gm = new GexMessage(msg, "sg", nonce);
//        System.out.println("CLIENT NONCE: " + nonce);

        receivedSignedMessages.put(gm.getNonce(), new ArrayList<>());

        for (Party p : parties) {
            sendMessage(gm, p.getAddress(), p.getPort());
        }
    }

    public void sendCheckMessage(String msg, ArrayList<String> signs) throws IOException,
            NoSuchAlgorithmException {
        String nonce = RandomGenerator.generateString(GexMessage.NONCE_LEN);
        GexMessage gm = new GexMessage(msg, "ch", nonce, signs);
        System.out.println(signs);
        System.out.println("Client is sending check message: " + gm);
        for (Party p : parties) {
            sendMessage(gm, p.getAddress(), p.getPort());
        }
    }

    @Override
    public void processMessage(GexMessage gm, String address, int port) {

        ArrayList<GexMessage> messages = receivedSignedMessages.get(gm.getNonce());
        messages.add(gm);

//        System.out.println("------------------------");
//        System.out.println("CLIENT GOT MESSAGE:\n" + gm);
//        System.out.println("CLIENT SIGNED MESSAGES:\n" + messages.size());
//        System.out.println("------------------------");

        if (messages.size() == parties.size()) {
            ArrayList<String> signs = new ArrayList<>();
            for (GexMessage msg : messages) {
                signs.add(msg.getSign(0));
            }

            try {
                sendCheckMessage(gm.getMessage(), signs);
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }


    }
}
