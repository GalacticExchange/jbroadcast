package udp;

import java.io.IOException;
import java.net.*;
import udp.MessagePacket;

public class UDPClient {

    private DatagramSocket socket;

    public static final int BUFFER_LENGTH = 1024;


    public UDPClient(String addr, int port) throws UnknownHostException, SocketException {
        InetAddress address = InetAddress.getByName(addr);
        socket = new DatagramSocket(port, address);
    }

    public void sendMessage(String msg, String addr, int port) throws IOException {
        InetAddress address = InetAddress.getByName(addr);
        byte[] msgBytes = msg.getBytes();

        DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length, address, port);
        socket.send(packet);
    }

    //TODO throws Exception -> change to customException
    public void sendMsg(String msg, String addr, int port) throws Exception {
//        MessagePacket mp = new MessagePacket(msg);
    }


    /**
     * blocks thread until message is received
     *
     * @return message
     */
    public String receiveMessage() throws IOException {
        byte[] buffer = new byte[BUFFER_LENGTH];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        return parsePacket(packet);
    }

    private String parsePacket(DatagramPacket p) {
        return new String(p.getData(), 0, p.getLength());
    }


}
