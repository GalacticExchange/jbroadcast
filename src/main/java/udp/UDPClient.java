package udp;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDPClient {

    private DatagramSocket socket;

    public UDPClient(String addr, int port) throws UnknownHostException, SocketException {
        InetAddress address = InetAddress.getByName(addr);
        socket = new DatagramSocket(port, address);
    }

    //TODO throws Exception -> change to customException
    public void sendMessage(String msg, String command, String addr, int port) throws Exception {
        //String NONCE = RandomGenerator.generateString(MessagePacket.NONCE_LEN);
        byte[] NONCE = RandomGenerator.generateByteArray(MessagePacket.NONCE_LEN);
        System.out.println("NONCE: " + NONCE);
        System.out.println("NONCE hashCode: " + Arrays.hashCode(NONCE));
        System.out.println("NONCE length: " + NONCE.length);

        MessagePacket[] mPackets = MessagePacket.splitMessage(msg, NONCE, command);
        for (MessagePacket mp : mPackets) {
            sendData(mp.getBytes(), addr, port);
        }
    }

    private void sendData(byte[] data, String addr, int port) throws IOException {
        InetAddress address = InetAddress.getByName(addr);
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
    }


    /**
     * blocks thread until message is received
     *
     * @return messagePacket
     */
    // TODO throws Exception -> change to customException
    public MessagePacket receiveMessage() throws Exception {
        byte[] buffer = new byte[MessagePacket.PACKET_LEN];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

//        System.out.println(parsePacket(packet));

        return new MessagePacket(packet.getData());
    }


    private String parsePacket(DatagramPacket p) {
        return new String(p.getData(), 0, p.getLength());
    }


}
