import java.io.IOException;

public class PartyReceive {

    public static void main(String[] args) {
        try {
            Party party = new Party("localhost", 1401);
            System.out.println(party.receiveMessage());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
