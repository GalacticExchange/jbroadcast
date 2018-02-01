package udp;

import java.io.IOException;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class UDPClient {

    private DatagramSocket socket;

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
     * blocks thread until message is received*
     */
    public FragmentPacket receiveMessage() throws IOException {
        byte[] buffer = new byte[FragmentPacket.PACKET_LEN];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        return new FragmentPacket(packet.getData(), packet.getAddress().toString(), packet.getPort());
    }

    public String getAddress() {
        return socket.getInetAddress().toString();
    }

    public int getPort() {
        return socket.getPort();
    }


    private String parsePacket(DatagramPacket p) {
        return new String(p.getData(), 0, p.getLength());
    }


}
