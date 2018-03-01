package udp;

import java.io.IOException;
import java.net.*;

public class UDPClient {

    private DatagramSocket socket;
    private byte[] buffer = new byte[SkaleMessage.PACKET_LEN];
    public UDPClient(String addr, int port) throws UnknownHostException, SocketException {
        InetAddress address = InetAddress.getByName(addr);
        socket = new DatagramSocket(port, address);
    }

    public void sendData(byte[] data, String addr, int port) throws IOException {
        InetAddress address = InetAddress.getByName(addr);
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
    }


    /**
     * blocks thread until message is received
     */
    public Packet receivePacket() throws IOException {
        byte[] buffer = new byte[Packet.PACKET_LEN];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        return new Packet(packet.getData(), packet.getAddress().getHostAddress(), packet.getPort());
    }

    /**
     * blocks thread until message is received
     */
    public SkaleMessage receiveSkaleMessage() throws IOException {
//        byte[] buffer = new byte[SkaleMessage.PACKET_LEN];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return SkaleMessage.parse(packet.getData());
    }

    public String getAddress() {
        return socket.getLocalAddress().getHostAddress();
    }

    public int getPort() {
        return socket.getLocalPort();
    }

}
