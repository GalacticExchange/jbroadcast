package utils;

import config.ReliablePartyConfig;
import config.ReliableSenderConfig;
import config.VerifiablePartyConfig;
import config.VerifiableSenderConfig;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.TimeUnit;

public class CLIUtils {

    private static final String HEADER = "Run broadcast\n\n";
    private static final String FOOTER = "\nPlease report issues at http://galacticexchange.io";

    private Options options;
    private HelpFormatter formatter;
    private CommandLineParser parser;

    public CLIUtils() {
        initOptions();
        formatter = new HelpFormatter();
        parser = new DefaultParser();
    }


    private void initOptions() {
        options = new Options();
        options.addRequiredOption("c", "config", true, "path to YAML config");
        options.addRequiredOption("b", "broadcast", true, "broadcast type {reliable , verifiable}");
        options.addOption("s", "sender", false, "run sender");
        options.addOption("p", "party", false, "run party");
        options.addOption("h", "help", false, "show this help");
    }


    private void showHelp() {
        formatter.printHelp("broadcast", HEADER, options, FOOTER, true);
    }

    public void parse(String[] args) throws ParseException, IOException, NoSuchAlgorithmException,
            InvalidKeySpecException, InterruptedException {
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (MissingOptionException ignored) {
            showHelp();
            return;
        }

        String confPath = cmd.getOptionValue("c");

        if (cmd.hasOption("s")) {
            runSender(confPath, cmd.getOptionValue("b"));
        } else if (cmd.hasOption("p")) {
            runParty(confPath, cmd.getOptionValue("b"));
        } else if (cmd.hasOption("h")) {
            showHelp();
        } else {
            System.out.println("Either sender or party must be provided as option!\n");
            showHelp();
        }


    }

    private void runSender(String confPath, String broadcast) throws IOException, NoSuchAlgorithmException, InterruptedException {
        if (broadcast.equals("reliable")) {

            ReliableSenderConfig config = ReliableSenderConfig.load(confPath);
            reliable.Client client = reliable.Client.createClient(config);
            // todo client send N messages
            runReliableTests(client);

        } else if (broadcast.equals("verifiable")) {
            VerifiableSenderConfig config = VerifiableSenderConfig.load(confPath);
            verifiable.Client client = verifiable.Client.createClient(config);
            // todo client send N messages

        }
    }

    private void runParty(String confPath, String broadcast) throws IOException, InvalidKeySpecException,
            NoSuchAlgorithmException {
        if (broadcast.equals("reliable")) {
            ReliablePartyConfig config = ReliablePartyConfig.load(confPath);
            System.out.println(config);
            reliable.Party party = reliable.Party.createParty(config);
            party.receiveMessage();

        } else if (broadcast.equals("verifiable")) {
            VerifiablePartyConfig config = VerifiablePartyConfig.load(confPath);
            System.out.println(config);
            verifiable.Party party = verifiable.Party.createParty(config);
            party.receiveMessage();
        } else {
            System.out.println("Unknown broadcast type");
        }
    }

    public static void runReliableTests(reliable.Client c) throws IOException, NoSuchAlgorithmException, InterruptedException {
        String msgs[] = new String[reliable.Party.TEST_AMOUNT_MESSAGES];
        for (int i = 0; i < reliable.Party.TEST_AMOUNT_MESSAGES; i++) {
            msgs[i] = udp.RandomGenerator.generateString(10);
        }

        for (int i = 0; i < reliable.Party.TEST_AMOUNT_MESSAGES; i++) {
            TimeUnit.MILLISECONDS.sleep(1);
//            System.out.println("sending message: " + msgs[i]);
            c.sendMessage(msgs[i]);
        }
    }
}
