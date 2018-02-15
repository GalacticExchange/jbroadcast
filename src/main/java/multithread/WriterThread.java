package multithread;

import udp.FragmentPacket;

import udp.GexMessage;
import udp.RandomGenerator;
import udp.UDPClient;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class WriterThread implements Runnable {

    private BlockingQueue<List<Object>> writerQueue;
    private UDPClient udpClient;

    public WriterThread(BlockingQueue<List<Object>> writerQueue, UDPClient udpClient) {
        this.udpClient = udpClient;
        this.writerQueue = writerQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                List<Object> l = writerQueue.take();
                sendMessage((GexMessage) l.get(0), (String) l.get(1), (Integer) l.get(2));
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void sendMessage(GexMessage gm, String address, int port) throws IOException {
        String nonce = RandomGenerator.generateString(GexMessage.NONCE_LEN);
        FragmentPacket[] packets = FragmentPacket.splitMessage(gm, nonce);
        for (FragmentPacket fp : packets) {
            sendPacket(fp, address, port);
        }
    }

    private void sendPacket(FragmentPacket fp, String address, int port) throws IOException {
        udpClient.sendData(fp.getBytes(), address, port);
    }
}
