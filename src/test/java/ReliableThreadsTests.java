import reliable.Client;
import reliable.Party;
import udp.RandomGenerator;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class ReliableThreadsTests {
    public static void main(String[] args) throws IOException {
        ArrayList<Party> parties = new ArrayList<>();

        int amountNodes = 5;

        for (int i = 0; i < amountNodes; i++) {
            parties.add(new Party("127.0.0.1", 1414 + i, "node" + i));

        }
        for (int i = 0; i < amountNodes; i++) {
            for (int j = 0; j < amountNodes; j++) {
                if (j != i) {
                    Party remote = Party.remoteParty(parties.get(j).getAddress(), parties.get(j).getPort(),
                            parties.get(j).getPartyId());
                    parties.get(i).addParty(remote);
                }
            }
        }

        Client c = new Client("127.0.0.1", 1515, parties);


        String msgs[] = new String[Party.TEST_AMOUNT_MESSAGES];
        for (int i = 0; i < Party.TEST_AMOUNT_MESSAGES; i++) {
            msgs[i] = RandomGenerator.generateString(10);
        }

        for (int i = 0; i < Party.TEST_AMOUNT_MESSAGES; i++) {
//            TimeUnit.NANOSECONDS.sleep(1);
            c.sendMessage(msgs[i]);
        }
        System.out.println("Client finished sending messages");
    }
}
