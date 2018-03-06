package reliable;

import config.ReliableSenderConfig;
import reliable.multithreaded.WriterThread;
import udp.Communicator;
import udp.GexMessage;
import udp.RandomGenerator;
import udp.SkaleMessage;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends Communicator {
    private ArrayList<Party> parties;

    private BlockingQueue<HashMap<String, Object>> writerQueue;
    private Runnable writer;

    public Client(String addr, int port, ArrayList<Party> parties) throws SocketException, UnknownHostException {
        super(addr, port);
        this.parties = parties;

        writerQueue = new LinkedBlockingQueue<>();
        writer = new WriterThread(writerQueue, udpClient);

        new Thread(writer).start();
    }


    public void sendMessage(String msg) throws IOException {
        String nonce = RandomGenerator.generateString(SkaleMessage.NONCE_LEN);
        SkaleMessage sm = new SkaleMessage(msg, "in", nonce);

        for (Party p : parties) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("message", sm);
            map.put("address", p.getAddress());
            map.put("port", p.getPort());
            writerQueue.add(map);
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
