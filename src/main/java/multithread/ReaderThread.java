package multithread;

import udp.FragmentPacket;
import udp.UDPClient;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class ReaderThread implements Runnable {

    //    private BlockingQueue<DatagramPacket> blockingQueue;
    private BlockingQueue<FragmentPacket> readQueue;
    private UDPClient udpClient;

    public ReaderThread(BlockingQueue<FragmentPacket> readQueue, UDPClient udpClient) {
        this.udpClient = udpClient;
        this.readQueue = readQueue;
    }


    @Override
    public void run() {
        while (true) {
            FragmentPacket fp = null;
            try {
                fp = udpClient.receiveMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fp != null) {
                readQueue.add(fp);
            }
        }
    }

}
