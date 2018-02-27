package reliable;

import config.ReliableSenderConfig;
import udp.Communicator;
import udp.GexMessage;
import udp.RandomGenerator;
import udp.SkaleMessage;

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


    public void sendMessage(String msg) throws IOException {
        String nonce = RandomGenerator.generateString(SkaleMessage.NONCE_LEN);
        SkaleMessage sm = new SkaleMessage(msg, "in", nonce);

        for (PartyMain p : parties) {
//            System.out.println(String.format("Sending message %s to party %s ", gm, p));
            sendMessage(sm, p.getAddress(), p.getPort());
        }
    }

    public static ClientMain createClient(ReliableSenderConfig config) throws SocketException, UnknownHostException {

        ArrayList<PartyMain> parties = new ArrayList<>();

        for (Map partyConf : config.getParties()) {
            String addr = (String) partyConf.get("address");
            Integer p = (Integer) partyConf.get("port");
            String id = (String) partyConf.get("id");
            parties.add(PartyMain.remoteParty(addr, p, id));
        }


        return new ClientMain(config.getAddress(), config.getPort(), parties);
    }

    @Override
    public void processMessage(GexMessage gm, String address, int port) {

    }
}
