import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import reliable.Party;
import reliable.Client;
import udp.RandomGenerator;

public class ReliableRound {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        ArrayList<Party> parties = new ArrayList<>();

        int amountNodes = 5;

        for (int i = 0; i < amountNodes; i++) {
            parties.add(new Party("127.0.0.1", 1414 + i, "node" + i));

        }
        for (int i = 0; i < amountNodes; i++) {
            for (int j = 0; j < amountNodes; j++) {
                if (j != i) {
                    parties.get(i).addParty(parties.get(j));
                }
            }
        }
        for (Party p : parties) {

            new Thread(() -> {
                try {
                    p.receiveMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }


        Client c = new Client("127.0.0.1", 1515, parties);


        String msgs[] = new String[Party.TEST_AMOUNT_MESSAGES];
        for (int i = 0; i < Party.TEST_AMOUNT_MESSAGES; i++) {
            msgs[i] = RandomGenerator.generateString(10);
        }

        for (int i = 0; i < Party.TEST_AMOUNT_MESSAGES; i++) {
            c.sendMessage(msgs[i]);
        }
    }
}
