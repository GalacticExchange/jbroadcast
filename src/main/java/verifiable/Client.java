package verifiable;

import config.VerifiableSenderConfig;
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
        receivedSignedMessages.put(gm.getNonce(), new ArrayList<>());

        for (Party p : parties) {
            sendMessage(gm, p.getAddress(), p.getPort());
        }
    }

    private void sendCheckMessage(String msg, String sendTime, HashMap<String, String> signs) throws IOException,
            NoSuchAlgorithmException {
        String nonce = RandomGenerator.generateString(GexMessage.NONCE_LEN);

        GexMessage gm = new GexMessage(msg, "ch", nonce, sendTime, signs);

        for (Party p : parties) {
            sendMessage(gm, p.getAddress(), p.getPort());
        }
    }

    @Override
    public void processMessage(GexMessage gm, String address, int port) {

        ArrayList<GexMessage> messages = receivedSignedMessages.get(gm.getNonce());
        messages.add(gm);

        if (messages.size() == parties.size()) {
            HashMap<String, String> signs = new HashMap<>();

            for (GexMessage msg : messages) {

                String key = (String) msg.getSigns().keySet().toArray()[0];
                String value = (String) msg.getSigns().values().toArray()[0];

                signs.put(key, value);

            }

            try {
                sendCheckMessage(gm.getMessage(), gm.getSendTime(), signs);
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }


    }

    public static Client createClient(VerifiableSenderConfig config) throws SocketException, UnknownHostException,
            NoSuchAlgorithmException {

        ArrayList<Party> parties = new ArrayList<>();

        for (Map partyConf : config.getParties()) {
            String addr = (String) partyConf.get("address");
            Integer p = (Integer) partyConf.get("port");
            String id = (String) partyConf.get("id");
            parties.add(new Party(addr, p, id));
        }


        return new Client(config.getAddress(), config.getPort(), parties);
    }
}
