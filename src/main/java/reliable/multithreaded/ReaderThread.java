package reliable.multithreaded;

import udp.SkaleMessage;
import udp.UDPClient;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class ReaderThread implements Runnable {

//    private BlockingQueue<FragmentPacket> readQueue;
    private BlockingQueue<SkaleMessage> readQueue;
    private UDPClient udpClient;

    public ReaderThread(BlockingQueue<SkaleMessage> readQueue, UDPClient udpClient) {
        this.udpClient = udpClient;
        this.readQueue = readQueue;
    }


//    @Override
//    public void run() {
//        while (true) {
//            FragmentPacket fp = null;
//            try {
//                fp = udpClient.receiveMessage();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (fp != null) {
//                readQueue.add(fp);
//            }
//        }
//    }

    @Override
    public void run() {
        while (true) {
            SkaleMessage sm = null;
            try {
                sm = udpClient.receiveSkaleMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (sm != null) {
                readQueue.add(sm);
            }
        }
    }

}
