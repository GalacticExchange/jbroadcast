import reliable.Client;
import reliable.ClientMain;
import reliable.PartyMain;
import udp.RandomGenerator;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class ReliableThreadsTests {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        ArrayList<PartyMain> parties = new ArrayList<>();

        int amountNodes = 5;

        for (int i = 0; i < amountNodes; i++) {
            parties.add(new PartyMain("127.0.0.1", 1414 + i, "node" + i));

        }
        for (int i = 0; i < amountNodes; i++) {
            for (int j = 0; j < amountNodes; j++) {
                if (j != i) {
                    PartyMain remote = PartyMain.remoteParty(parties.get(j).getAddress(), parties.get(j).getPort(),
                            parties.get(j).getPartyId());
                    parties.get(i).addParty(remote);
                }
            }
        }
//        for (PartyMain p : parties) {
//
//            new Thread(() -> {
//                try {
//                    p.receiveMessage();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        }


        ClientMain c = new ClientMain("127.0.0.1", 1515, parties);


        String msgs[] = new String[PartyMain.TEST_AMOUNT_MESSAGES];
        for (int i = 0; i < PartyMain.TEST_AMOUNT_MESSAGES; i++) {
            msgs[i] = RandomGenerator.generateString(10);
        }

        for (int i = 0; i < PartyMain.TEST_AMOUNT_MESSAGES; i++) {
            c.sendMessage(msgs[i]);
        }
    }
}
