package reliable;

import udp.Communicator;
import udp.GexMessage;
import udp.RandomGenerator;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Client extends Communicator {

    private ArrayList<Party> parties;

    public Client(String addr, int port, ArrayList<Party> parties) throws SocketException, UnknownHostException {
        super(addr, port);
        this.parties = parties;
    }


    public void sendMessage(String msg) throws IOException, NoSuchAlgorithmException {
        String nonce = RandomGenerator.generateString(GexMessage.NONCE_LEN);
        GexMessage gm = new GexMessage(msg, "in", nonce);

        for (Party p : parties) {
            sendMessage(gm, p.getAddress(), p.getPort());
        }
    }

    @Override
    public void processMessage(GexMessage gm, String address, int port) {

    }
}
