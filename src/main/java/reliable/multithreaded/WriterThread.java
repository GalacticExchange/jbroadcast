package reliable.multithreaded;

import org.apache.commons.lang3.StringUtils;
import udp.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class WriterThread implements Runnable {

    private BlockingQueue<HashMap<String, Object>> writerQueue;
    private UDPClient udpClient;

    public WriterThread(BlockingQueue<HashMap<String, Object>> writerQueue, UDPClient udpClient) {
        this.udpClient = udpClient;
        this.writerQueue = writerQueue;
    }

    @Override
    public void run() {
        while (true) {

            ArrayList<HashMap<String, Object>> pendingSend = new ArrayList<>();
            writerQueue.drainTo(pendingSend, 10);
            processPendingSend(pendingSend);

//                HashMap<String, Object> map = writerQueue.take();
//                SkaleMessage sm = (SkaleMessage) map.get("message");
//                String address = (String) map.get("address");
//                Integer port = (Integer) map.get("port");
//                sendSkaleMessage(sm, address, port);


        }
    }

    private void processPendingSend(ArrayList<HashMap<String, Object>> pendingSend) {
        HashMap<String, ArrayList<SkaleMessage>> combined = combine(pendingSend);
        HashMap<String, BatchMessages> batchMessagesMap = createBatchMessages(combined);
        HashMap<String, Packet> packets = createPackets(batchMessagesMap);
        sendPackets(packets);
    }

    private void sendPackets(HashMap<String, Packet> packets) {
        packets.forEach((String k, Packet v) -> {
            String address = StringUtils.substringBefore(k, ":");
            Integer port = Integer.valueOf(StringUtils.substringAfter(k, ":"));
            try {
                sendPacket(v, address, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private HashMap<String, Packet> createPackets(HashMap<String, BatchMessages> batchMessagesMap) {
        HashMap<String, Packet> packets = new HashMap<>();
        batchMessagesMap.forEach((k, v) -> {
            packets.put(
                    k, Packet.batchToPacket(v, RandomGenerator.generateString(Packet.NONCE_LEN))
            );
        });
        return packets;
    }

    private HashMap<String, BatchMessages> createBatchMessages(HashMap<String, ArrayList<SkaleMessage>> combined) {
        HashMap<String, BatchMessages> batchMessagesMap = new HashMap<>();
        combined.forEach((k, v) -> batchMessagesMap.put(k, new BatchMessages(v)));
        return batchMessagesMap;
    }

    private HashMap<String, ArrayList<SkaleMessage>> combine(ArrayList<HashMap<String, Object>> messages) {
        HashMap<String, ArrayList<SkaleMessage>> combined = new HashMap<>();
        for (HashMap<String, Object> map : messages) {
            // todo kludge "ip:port" as key
            String key = String.format("%s:%s", map.get("address"), map.get("port"));
            if (!combined.containsKey(key)) {
                combined.put(key, new ArrayList<>());
            }
            combined.get(key).add((SkaleMessage) map.get("message"));
        }
        return combined;
    }


    private void sendSkaleMessage(SkaleMessage gm, String address, int port) throws IOException {
//        String nonce = RandomGenerator.generateString(GexMessage.NONCE_LEN);
//        Packet[] packets = Packet.splitMessage(gm, nonce);
//        for (Packet fp : packets) {
//            sendPacket(fp, address, port);
//        }

//        byte [] data = new byte[SkaleMessage.PACKET_LEN];
        udpClient.sendData(gm.getBytes(), address, port);
    }

    private void sendPacket(Packet p, String address, int port) throws IOException {
        udpClient.sendData(p.getBytes(), address, port);
    }
}
