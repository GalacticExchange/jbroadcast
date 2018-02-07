import org.apache.commons.cli.ParseException;
import utils.CLIUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {

    public static void main(String[] args) throws ParseException, IOException, NoSuchAlgorithmException {
        CLIUtils cli = new CLIUtils();
        String cmdArgs[] = new String[]{"-c", "config/client.yml", "-b", "reliable", "-s"};
        cli.parse(cmdArgs);

//        String cmdArgs2[] = new String[]{"-c", "config/client.yml", "-b", "verifiable", "-s"};
//        cli.parse(cmdArgs2);
    }
}
