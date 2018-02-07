package utils;

import config.ReliablePartyConfig;
import config.ReliableSenderConfig;
import config.VerifiableSenderConfig;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

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
        options.addRequiredOption("c", "config", true, "path to config");
        options.addRequiredOption("b", "broadcast", true, "broadcast type {reliable , verifiable}");
        options.addOption("s", "sender", false, "run sender");
        options.addOption("p", "party", false, "run party");
    }


    private void help() {
        formatter.printHelp("broadcast", HEADER, options, FOOTER, true);
    }

    public void parse(String[] args) throws ParseException, IOException, NoSuchAlgorithmException {
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (MissingOptionException ignored) {
            help();
            return;
        }

        String confPath = cmd.getOptionValue("c");

        if (cmd.hasOption("s")) {
            runSender(confPath, cmd.getOptionValue("b"));
        } else if (cmd.hasOption("p")) {
            // run new party
        } else {
            System.out.println("Either sender or party must be provided as option!\n");
            help();
        }


    }

    private void runSender(String confPath, String broadcast) throws IOException, NoSuchAlgorithmException {
        if (broadcast.equals("reliable")) {

            ReliableSenderConfig relConfig = ReliableSenderConfig.load(confPath);
            reliable.Client client = reliable.Client.createClient(relConfig);
            // todo client send N messages

        } else if (broadcast.equals("verifiable")) {
            VerifiableSenderConfig verConfig = VerifiableSenderConfig.load(confPath);
            verifiable.Client client = verifiable.Client.createClient(verConfig);
            // todo client send N messages

        }
    }

//    private void runParty(String confPath, String broadcast) throws IOException {
//        if (broadcast.equals("reliable")) {
//            ReliablePartyConfig relConfig = ReliablePartyConfig.load(confPath);
//            reliable.Party party = reliable.Party.createParty(relConfig);
//            // todo client send N messages
//
//        } else if (broadcast.equals("verifiable")) {
//
//            // todo client send N messages
//
//        }
//    }
}
