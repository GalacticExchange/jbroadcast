import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class MainTest {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, ParseException, IOException {
        String cmdArgs[] = new String[]{"-c", "config_example/party_verifiable.yml", "-b", "verifiable", "-p"};

        Main.processCmd(cmdArgs);
    }
}
