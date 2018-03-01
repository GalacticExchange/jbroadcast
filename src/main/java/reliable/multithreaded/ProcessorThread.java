package reliable.multithreaded;

import com.google.protobuf.InvalidProtocolBufferException;
import reliable.Party;
import udp.BatchMessages;
import udp.Packet;
import udp.SkaleMessage;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.BlockingQueue;


/**
 * Assemble fragments -> pass them to Writer or Commit
 */
//public class ProcessorThread extends Messenger implements Runnable {
public class ProcessorThread implements Runnable {

    private BlockingQueue<Packet> readerQueue;
    //    private BlockingQueue<SkaleMessage> readerQueue;
    private BlockingQueue<HashMap<String, Object>> writerQueue;

    private HashMap<String, Integer> receivedEchos;
    private HashMap<String, LinkedList<SkaleMessage>> receivedReadies;
    private Set<String> sentReadies;
    private List<Party> parties;

    private List<SkaleMessage> committedMessages;

    // TODO !
    private int n = 5;
    private int t = 1;


    public ProcessorThread(BlockingQueue<Packet> readerQueue, BlockingQueue<HashMap<String, Object>> writerQueue,
                           List<Party> parties, List<SkaleMessage> committedMessages) {
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
                Packet packet = readerQueue.take();
//                ArrayList<Packet> arr = new ArrayList<>();
//                readerQueue.drainTo(arr, 10);

                processPacket(packet);
            } catch (InterruptedException | InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }

    }

//    @Override
//    public void run() {
//        while (true) {
//            try {
//                SkaleMessage sm = readerQueue.take();
////                ArrayList<Packet> arr = new ArrayList<>();
////                readerQueue.drainTo(arr, 10);
//
//                processMessage(sm);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

    private void processPacket(Packet packet) throws InvalidProtocolBufferException {
        BatchMessages batchMessages = BatchMessages.parse(packet.getData());
        batchMessages.getMessages().forEach((this::processMessage));
    }


    //    public void processMessage(GexMessage gm, String address, int port) {
    private void processMessage(SkaleMessage gm) {

        switch (gm.getCommand()) {
            case "in":
                SkaleMessage echo = new SkaleMessage(gm.getMessage(), "ec", gm.getNonce());
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

    }


    //    private void checkEcho(GexMessage gm) {
    private void checkEcho(SkaleMessage gm) {

        if (!receivedEchos.containsKey(gm.getNonce())) {
//            receivedEchos.put(gm.getNonce(), new ArrayList<>());
            receivedEchos.put(gm.getNonce(), 1);
            return;
        }

        int echos = receivedEchos.get(gm.getNonce()) + 1;
        receivedEchos.put(gm.getNonce(), echos);

        if (echos >= (n + t + 1) / 2 && !sentReadies.contains(gm.getNonce())) {
            SkaleMessage ready = new SkaleMessage(gm.getMessage(), "rd", gm.getNonce());
            sendToParties(ready);
            sentReadies.add(gm.getNonce());
        }
    }

    //    private void checkReady(GexMessage gm) {
    private void checkReady(SkaleMessage gm) {
        if (!receivedReadies.containsKey(gm.getNonce())) {
//            receivedReadies.put(gm.getNonce(), new ArrayList<>());
            receivedReadies.put(gm.getNonce(), new LinkedList<>());
            return;
        }

        receivedReadies.get(gm.getNonce()).add(gm);
        int readies = receivedReadies.get(gm.getNonce()).size();

        if (readies >= t + 1 && !sentReadies.contains(gm.getNonce())) {
            SkaleMessage ready = new SkaleMessage(gm.getMessage(), "rd", gm.getNonce());
            sendToParties(ready);
            sentReadies.add(gm.getNonce());
            receivedReadies.remove(gm.getNonce());
        }

        if (readies == 2 * t + 1) {
            committedMessages.add(gm);
//            if (committedMessages.size() % 10000 == 0) {
                System.out.println(String.format("Committed amount: %s", committedMessages.size()));
//            }

        }
    }


    //    private void sendToParties(GexMessage gm) {
    private void sendToParties(SkaleMessage sm) {
        for (Party p : parties) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("message", sm);
            map.put("address", p.getAddress());
            map.put("port", p.getPort());
            writerQueue.add(map);
        }
    }

    private void checkTime() {
//        TODO
        if (committedMessages.size() == Party.TEST_AMOUNT_MESSAGES) {
//        if (committtedCount == Party.TEST_AMOUNT_MESSAGES) {

            Instant startTime = Instant.parse(committedMessages.get(0).getSendTime());
            Instant finishTime = Instant.now();

            Duration timeElapsed = Duration.between(startTime, finishTime);


            System.out.println(String.format("Party %s : %s messages elapsed time: %s ", this,
                    Party.TEST_AMOUNT_MESSAGES, timeElapsed));
        }
    }

}
