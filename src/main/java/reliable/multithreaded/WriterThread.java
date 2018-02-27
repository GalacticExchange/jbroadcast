package reliable.multithreaded;

import udp.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

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
            try {
//                ArrayList<Object> arr = new ArrayList<>();
//                writerQueue.drainTo(arr,10);
//                System.out.println(arr.size());

                HashMap<String, Object> map = writerQueue.take();
                SkaleMessage gm = (SkaleMessage) map.get("message");
                String address = (String) map.get("address");
                Integer port = (Integer) map.get("port");
                sendMessage(gm, address, port);

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void sendMessage(SkaleMessage gm, String address, int port) throws IOException {
//        String nonce = RandomGenerator.generateString(GexMessage.NONCE_LEN);
//        FragmentPacket[] packets = FragmentPacket.splitMessage(gm, nonce);
//        for (FragmentPacket fp : packets) {
//            sendPacket(fp, address, port);
//        }
        // todo fixed size!
//        byte [] data = new byte[SkaleMessage.PACKET_LEN];
        int length = gm.getBytes().length;
        udpClient.sendData(gm.getBytes(), address, port);
    }

//    private void sendPacket(FragmentPacket fp, String address, int port) throws IOException {
//        udpClient.sendData(fp.getBytes(), address, port);
//    }
}
