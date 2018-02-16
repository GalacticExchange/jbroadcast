package reliable.multithreaded;

import com.google.protobuf.InvalidProtocolBufferException;
import reliable.PartyMain;
import udp.FragmentPacket;
import udp.GexMessage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.BlockingQueue;


/**
 * Assemble fragments and pass them to Writer
 */
public class ProcessorThread extends Messenger implements Runnable {

    private BlockingQueue<FragmentPacket> readerQueue;
    private BlockingQueue<HashMap<String, Object>> writerQueue;

    private HashMap<String, ArrayList<GexMessage>> receivedEchos;
    private HashMap<String, ArrayList<GexMessage>> receivedReadies;
    private Set<String> sentReadies;
    private ArrayList<PartyMain> parties;

    private ArrayList<GexMessage> committedMessages;

    // TODO !
    private int n = 5;
    private int t = 1;


    public ProcessorThread(BlockingQueue<FragmentPacket> readerQueue, BlockingQueue<HashMap<String, Object>> writerQueue,
                           ArrayList<PartyMain> parties, ArrayList<GexMessage> committedMessages) {
        this.readerQueue = readerQueue;
        this.writerQueue = writerQueue;
        this.committedMessages = committedMessages;
        this.parties = parties;

        receivedEchos = new HashMap<>();
        receivedReadies = new HashMap<>();
        sentReadies = new HashSet<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                FragmentPacket fp = readerQueue.take();
                processFragment(fp);
            } catch (InterruptedException | InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }

    }


    public void processMessage(GexMessage gm, String address, int port) {
        try {

            switch (gm.getCommand()) {
                case "in":
                    GexMessage echo = new GexMessage(gm.getMessage(), "ec", gm.getNonce());
                    sendToParties(echo);
                    break;

                case "ec":
                    checkEcho(gm);
                    break;

                case "rd":
                    checkReady(gm);
                    checkTime();

                    break;
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }


    private void checkEcho(GexMessage gm) {

        if (!receivedEchos.containsKey(gm.getNonce())) {
            receivedEchos.put(gm.getNonce(), new ArrayList<>());
            return;
        }

        receivedEchos.get(gm.getNonce()).add(gm);

        int echos = receivedEchos.get(gm.getNonce()).size();

        if (echos >= (n + t + 1) / 2 && !sentReadies.contains(gm.getNonce())) {
            GexMessage ready = new GexMessage(gm.getMessage(), "rd", gm.getNonce());
            sendToParties(ready);
            sentReadies.add(gm.getNonce());
        }
    }

    private void checkReady(GexMessage gm) throws IOException, NoSuchAlgorithmException {
        if (!receivedReadies.containsKey(gm.getNonce())) {
            receivedReadies.put(gm.getNonce(), new ArrayList<>());
            return;
        }

        receivedReadies.get(gm.getNonce()).add(gm);
        int readies = receivedReadies.get(gm.getNonce()).size();

        if (readies >= t + 1 && !sentReadies.contains(gm.getNonce())) {
            GexMessage ready = new GexMessage(gm.getMessage(), "rd", gm.getNonce());
            sendToParties(ready);
            sentReadies.add(gm.getNonce());
        }

        if (readies >= 2 * t + 1) {
            committedMessages.add(gm);
            System.out.println(String.format("Committed amount: %s", committedMessages.size()));

        }
    }


    private void sendToParties(GexMessage gm) {
        for (PartyMain p : parties) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("message", gm);
            map.put("address", p.getAddress());
            map.put("port", p.getPort());
            writerQueue.add(map);
        }
    }

    private void checkTime() {
        if (committedMessages.size() == PartyMain.TEST_AMOUNT_MESSAGES) {

            Instant startTime = Instant.parse(committedMessages.get(0).getSendTime());
            Instant finishTime = Instant.now();

            Duration timeElapsed = Duration.between(startTime, finishTime);


            System.out.println(String.format("Party %s : %s messages elapsed time: %s ", this,
                    PartyMain.TEST_AMOUNT_MESSAGES, timeElapsed));
        }
    }

}
