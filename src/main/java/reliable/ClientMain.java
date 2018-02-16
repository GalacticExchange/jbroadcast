package reliable;

import config.ReliableSenderConfig;
import udp.Communicator;
import udp.GexMessage;
import udp.RandomGenerator;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;

public class ClientMain extends Communicator {
    private ArrayList<PartyMain> parties;

    public ClientMain(String addr, int port, ArrayList<PartyMain> parties) throws SocketException, UnknownHostException {
        super(addr, port);
        this.parties = parties;
    }


    public void sendMessage(String msg) throws IOException, NoSuchAlgorithmException {
        String nonce = RandomGenerator.generateString(GexMessage.NONCE_LEN);
        GexMessage gm = new GexMessage(msg, "in", nonce);

        for (PartyMain p : parties) {
//            System.out.println(String.format("Sending message %s to party %s ", gm, p));
            sendMessage(gm, p.getAddress(), p.getPort());
        }
    }

    public static Client createClient(ReliableSenderConfig config) throws SocketException, UnknownHostException {

        ArrayList<Party> parties = new ArrayList<>();

        for (Map partyConf : config.getParties()) {
            String addr = (String) partyConf.get("address");
            Integer p = (Integer) partyConf.get("port");
            String id = (String) partyConf.get("id");
            parties.add(Party.remoteParty(addr, p, id));
        }


        return new Client(config.getAddress(), config.getPort(), parties);
    }

    @Override
    public void processMessage(GexMessage gm, String address, int port) {

    }
}
