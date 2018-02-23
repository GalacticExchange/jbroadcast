package reliable;

import config.ReliablePartyConfig;
import reliable.multithreaded.ProcessorThread;
import reliable.multithreaded.ReaderThread;
import reliable.multithreaded.WriterThread;
import udp.FragmentPacket;
import udp.GexMessage;
import udp.UDPClient;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PartyMain {
    private List<PartyMain> parties;


    private List<GexMessage> committedMessages;

    private UDPClient udpClient;

    private BlockingQueue<FragmentPacket> readerQueue;
    private BlockingQueue<HashMap<String, Object>> writerQueue;

    private Runnable reader;
    private Runnable processor;
    private Runnable writer;

    private String address;
    private int port;
    private String partyId;


//    public static final int TEST_AMOUNT_MESSAGES = 150_000;
    public static final int TEST_AMOUNT_MESSAGES = 10_000;

    public PartyMain(String address, int port, String partyId) throws SocketException, UnknownHostException {
        this.partyId = partyId;
//        committedMessages = new ArrayList<>();
        committedMessages = new LinkedList<>();
        parties = new LinkedList<>();
        initQueues();
        initUDP(address, port);
        initThreads();
        start();
    }

    private PartyMain() {

    }

    /**
     * Don't listen on address / port
     */
    public static PartyMain remoteParty(String address, int port, String partyId) {
        PartyMain p = new PartyMain();
        p.setAddress(address);
        p.setPort(port);
        p.setPartyId(partyId);

        return p;
    }


    public void addParty(PartyMain p) {
        parties.add(p);
    }

    private void initQueues() {
//        readerQueue = new ArrayBlockingQueue<>(512000);
//        writerQueue = new ArrayBlockingQueue<>(512000);

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
        new Thread(writer).start();
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

    public static PartyMain createParty(ReliablePartyConfig config) throws SocketException, UnknownHostException {

        PartyMain party = new PartyMain(config.getAddress(), config.getPort(), config.getId());


        for (Map partyConf : config.getParties()) {
            String addr = (String) partyConf.get("address");
            Integer p = (Integer) partyConf.get("port");
            String id = (String) partyConf.get("id");
            party.addParty(remoteParty(addr, p, id));
        }

        return party;
    }

}
