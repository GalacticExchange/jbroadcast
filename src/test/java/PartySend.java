import udp.GexMessage;


public class PartySend {

    public static void main(String[] args) throws Exception {

        Party party = new Party("localhost", 1401);

        String msg = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
//        party.sendSignMessage(msg, "localhost", 1414);
        party.sendMessage(new GexMessage(msg, "ch"), "localhost", 1400);

    }
}
