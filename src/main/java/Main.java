import org.apache.commons.cli.ParseException;
import utils.CLIUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Main {


    public static void processCmd(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, ParseException, IOException {
        CLIUtils cli = new CLIUtils();
        cli.parse(args);
    }

    public static void main(String[] args) throws ParseException, IOException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        processCmd(args);
    }
}
