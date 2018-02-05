import udp.RandomGenerator;

import java.util.ArrayList;

public class VerificationRound {

    public static void main(String[] args) throws Exception {

        ArrayList<Party> parties = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String keyDir = "parties/party" + i;
            parties.add(new Party(keyDir, "127.0.0.1", 1414 + i, "party" + i));
            for (int j = 0; j < 5; j++) {
                // todo add its own pub key? (check if i != j)
                parties.get(i).addPublicKeyToList("party" + j, "parties/party" + j + "/publicKey");
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
//            TimeUnit.MILLISECONDS.sleep(6);
//            c.sendSignMessage("TESTMESSAGE");
            c.sendSignMessage(msgs[i]);
        }

    }
}
