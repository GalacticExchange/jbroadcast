import java.util.ArrayList;

public class VerificationRound {

    public static void main(String[] args) throws Exception {

        ArrayList<Party> parties = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String keyDir = "parties/party" + i;
            parties.add(new Party(keyDir, "127.0.0.1", 1414 + i));
        }

        for (Party p : parties) {

            new Thread(() -> {
                try {
                    System.out.println(String.format("Party %s starting receiving messages..", p));
                    p.receiveMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        Client c = new Client("127.0.0.1", 1515);


    }
}
