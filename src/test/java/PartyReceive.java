import verifiable.Party;

public class PartyReceive {

    public static void main(String[] args) throws Exception {

        Party party = new Party("localhost", 1400, "party0");

        party.receiveMessage();


    }
}
