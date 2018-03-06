import udp.GexMessage;
import udp.RandomGenerator;
import verifiable.Party;


public class PartySend {

    public static void main(String[] args) throws Exception {

        Party party = new Party("localhost", 1401, "party0");

        String msg = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
        party.sendMessage(new GexMessage(msg, "ch", RandomGenerator.generateString(GexMessage.NONCE_LEN)),
                "localhost", 1400);

    }
}
