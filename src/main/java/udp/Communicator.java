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

    private UDPClient udpClient;
    private Map<Integer, ArrayList<FragmentPacket>> receivedFragments;


    public Communicator(String addr, int port) throws SocketException, UnknownHostException {
        udpClient = new UDPClient(addr, port);
        receivedFragments = new HashMap<>();
    }

    /**
     * blocking call
     */
    public void receiveMessage() throws IOException {
        while (true) {
            FragmentPacket fp = udpClient.receiveMessage();
            int nonceHashCode = fp.getNonceHashCode();

            if (!receivedFragments.containsKey(nonceHashCode)) {
                receivedFragments.put(nonceHashCode, new ArrayList<>());
            }

            receivedFragments.get(nonceHashCode).add(fp);

            if (isLastPacket(fp)) {
                GexMessage gm = assembleMessage(fp.getNonceHashCode());
                processMessage(gm, fp.getAddress(), fp.getPort());
            }

        }

    }

    private boolean isLastPacket(FragmentPacket fp) {
        // TODO the order could not be guaranteed.. check HashMap[nonce] length or smth...
        return fp.getIndex() + 1 == fp.getAmount();
    }

    private GexMessage assembleMessage(int nonceHashCode) throws InvalidProtocolBufferException {

        FragmentPacket[] packets = new FragmentPacket[receivedFragments.get(nonceHashCode).size()];
        receivedFragments.get(nonceHashCode).toArray(packets);

        GexMessage assembled = FragmentPacket.assembleMessage(packets);
//        System.out.println("GOT assembled message: " + assembled);

        return assembled;
    }

    public void sendMessage(GexMessage gm, String addr, int port) throws NoSuchAlgorithmException, IOException {
        byte[] NONCE = RandomGenerator.generateByteArray(FragmentPacket.NONCE_LEN);

//        System.out.println("NONCE hashCode: " + Arrays.hashCode(NONCE));
        FragmentPacket[] fPackets = FragmentPacket.splitMessage(gm, NONCE);

        for (FragmentPacket fp : fPackets) {
            udpClient.sendData(fp.getBytes(), addr, port);
        }
    }

    public String getAddress() {
        return udpClient.getAddress();
    }

    public int getPort() {
        return udpClient.getPort();
    }

    public abstract void processMessage(GexMessage gm, String address, int port);


}
