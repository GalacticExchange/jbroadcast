import udp.UDPClient;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Party {

    private UDPClient udpClient;

    // Todo: party.yml constructor?

    public Party(String addr, int port) throws SocketException, UnknownHostException {
        udpClient = new UDPClient(addr, port);
        // TODO ECDSA keyPair ?
    }

    public void sendMessage(String msg, String addr, int port) throws IOException {
        udpClient.sendMessage(msg, addr, port);
    }

    public String receiveMessage() throws IOException {
        return udpClient.receiveMessage();
    }

//    public static void main(String[] args) {
//        try {
//            Party party = new Party("localhost", 1400);
//        } catch (SocketException | UnknownHostException e) {
//            e.printStackTrace();
//        }
//
//    }
}
