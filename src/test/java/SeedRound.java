import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import verifiable.Party;

public class SeedRound {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

        for (int i = 0; i < 5; i++) {
            File dir = new File("parties/party" + i);
            dir.mkdir();
            new Party("127.0.0.1", 1414 + i, "party" + i).saveKeys(dir.toString());
        }


    }
}
