package udp;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Communicator {

    protected UDPClient udpClient;
    private Map<String, ArrayList<Packet>> receivedFragments;

    private String address;
    private int port;

    public Communicator(String address, int port) throws SocketException, UnknownHostException {
        this.address = address;
        this.port = port;

        udpClient = new UDPClient(address, port);
        receivedFragments = new HashMap<>();
    }

    public Communicator() {

    }

    /**
     * blocking call
     */
    public void receiveMessage() throws IOException {
        while (true) {
            Packet fp = udpClient.receivePacket();
            String nonce = fp.getNonce();

            if (!receivedFragments.containsKey(nonce)) {
                receivedFragments.put(nonce, new ArrayList<>());
            }

            receivedFragments.get(nonce).add(fp);

            if (isLastPacket(fp)) {
                GexMessage gm = assembleMessage(fp.getNonce());
                processMessage(gm, fp.getAddress(), fp.getPort());
            }

        }

    }

    private boolean isLastPacket(Packet fp) {
        // TODO the order could not be guaranteed.. check HashMap[nonce] length or smth...
        return fp.getIndex() + 1 == fp.getAmount();
    }

    private GexMessage assembleMessage(String nonce) throws InvalidProtocolBufferException {

        Packet[] packets = new Packet[receivedFragments.get(nonce).size()];
        receivedFragments.get(nonce).toArray(packets);

//        GexMessage assembled = Packet.assembleMessage(packets);

        return Packet.assembleMessage(packets);
    }

    public void sendMessage(GexMessage gm, String addr, int port) throws NoSuchAlgorithmException, IOException {
//        byte[] NONCE = RandomGenerator.generateByteArray(Packet.NONCE_LEN);
        String NONCE = RandomGenerator.generateString(Packet.NONCE_LEN);

        Packet[] fPackets = Packet.splitMessage(gm, NONCE);

        for (Packet fp : fPackets) {
            udpClient.sendData(fp.getBytes(), addr, port);
        }
    }

//    public void sendMessage(SkaleMessage sm, String addr, int port) throws IOException {
////        byte[] NONCE = RandomGenerator.generateByteArray(Packet.NONCE_LEN);
//        int length = sm.getBytes().length;
//        udpClient.sendData(sm.getBytes(), addr, port);
//    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public abstract void processMessage(GexMessage gm, String address, int port);


}
