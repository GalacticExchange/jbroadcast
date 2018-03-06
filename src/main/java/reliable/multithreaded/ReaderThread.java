package reliable.multithreaded;

import udp.Packet;
import udp.UDPClient;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class ReaderThread implements Runnable {

    private BlockingQueue<Packet> readerQueue;
    private UDPClient udpClient;

    public ReaderThread(BlockingQueue<Packet> readerQueue, UDPClient udpClient) {
        this.udpClient = udpClient;
        this.readerQueue = readerQueue;
    }


    @Override
    public void run() {
        while (true) {
            Packet fp = null;
            try {
                fp = udpClient.receivePacket();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fp != null) {
                readerQueue.add(fp);
            }
        }
    }

}
