package reliable;

import config.ReliablePartyConfig;
import reliable.multithreaded.ProcessorThread;
import reliable.multithreaded.ReaderThread;
import reliable.multithreaded.WriterThread;
import udp.Packet;
import udp.SkaleMessage;
import udp.UDPClient;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Party {
    private List<Party> parties;


    private List<SkaleMessage> committedMessages;

    private UDPClient udpClient;

    private BlockingQueue<Packet> readerQueue;
//    private BlockingQueue<SkaleMessage> readerQueue;
    private BlockingQueue<HashMap<String, Object>> writerQueue;

    private Runnable reader;
    private Runnable processor;
    private Runnable writer;

    private String address;
    private int port;
    private String partyId;


    public static final int TEST_AMOUNT_MESSAGES = 10_000;
//    public static final int TEST_AMOUNT_MESSAGES = 10_000;

    public Party(String address, int port, String partyId) throws SocketException, UnknownHostException {
        this.partyId = partyId;
//        committedMessages = new ArrayList<>();
        committedMessages = new LinkedList<>();
        parties = new LinkedList<>();
        initQueues();
        initUDP(address, port);
        initThreads();
        start();
    }

    private Party() {

    }

    /**
     * Don't listen on address / port
     */
    public static Party remoteParty(String address, int port, String partyId) {
        Party p = new Party();
        p.setAddress(address);
        p.setPort(port);
        p.setPartyId(partyId);

        return p;
    }


    public void addParty(Party p) {
        parties.add(p);
    }

    private void initQueues() {
        readerQueue = new LinkedBlockingQueue<>();
        writerQueue = new LinkedBlockingQueue<>();

    }

    private void initUDP(String address, int port) throws SocketException, UnknownHostException {
        this.address = address;
        this.port = port;
        udpClient = new UDPClient(address, port);
    }

    private void initThreads() {
        reader = new ReaderThread(readerQueue, udpClient);
        processor = new ProcessorThread(readerQueue, writerQueue, parties, committedMessages);
        writer = new WriterThread(writerQueue, udpClient);
    }

    private void start() {
        new Thread(reader).start();
        new Thread(processor).start();
        Thread t = new Thread(writer);
//        t.setPriority(3);
        t.start();
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getPartyId() {
        return partyId;
    }


    private void setAddress(String address) {
        this.address = address;
    }

    private void setPort(int port) {
        this.port = port;
    }


    private void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public static Party createParty(ReliablePartyConfig config) throws SocketException, UnknownHostException {

        Party party = new Party(config.getAddress(), config.getPort(), config.getId());


        for (Map partyConf : config.getParties()) {
            String addr = (String) partyConf.get("address");
            Integer p = (Integer) partyConf.get("port");
            String id = (String) partyConf.get("id");
            party.addParty(remoteParty(addr, p, id));
        }

        return party;
    }

}
