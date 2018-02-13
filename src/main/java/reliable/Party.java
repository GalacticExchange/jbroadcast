package reliable;

import config.ReliablePartyConfig;
import udp.Communicator;
import udp.GexMessage;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Party extends Communicator {

    private ArrayList<Party> parties;

    private HashMap<String, ArrayList<GexMessage>> receivedEchos;

    private HashMap<String, ArrayList<GexMessage>> receivedReadies;

    private Set<String> sentReadies;

    private ArrayList<GexMessage> committedMessages;

    private String partyId;

    private int n = 5;
    private int t = 1;

    public static final int TEST_AMOUNT_MESSAGES = 100;

    public Party(String addr, int port, String partyId) throws SocketException, UnknownHostException {
        super(addr, port);
        this.partyId = partyId;

        parties = new ArrayList<>();

        receivedEchos = new HashMap<>();
        receivedReadies = new HashMap<>();
        sentReadies = new HashSet<>();
        committedMessages = new ArrayList<>();

    }

    private Party() {

    }

    /**
     * Don't listen on address / port
     */
    public static Party remoteParty(String address, int port, String partyId) {
        Party p = new Party();
        p.setAddress(address);
        p.setPort(port);
        p.setPartyId(partyId);

        return p;
    }


    public void addParty(Party p) {
        parties.add(p);
    }

    @Override
    public void processMessage(GexMessage gm, String address, int port) {
        //todo
        System.out.println("Got message:\n" + gm);
        try {

            switch (gm.getCommand()) {
                case "in":
                    GexMessage echo = new GexMessage(gm.getMessage(), "ec", gm.getNonce());
                    sendToParties(echo);
                    break;

                case "ec":
                    checkEcho(gm);
                    break;

                case "rd":
                    checkReady(gm);
                    checkTime();

                    break;
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    private void checkEcho(GexMessage gm) throws IOException, NoSuchAlgorithmException {

        if (!receivedEchos.containsKey(gm.getNonce())) {
            receivedEchos.put(gm.getNonce(), new ArrayList<>());
            return;
        }

        receivedEchos.get(gm.getNonce()).add(gm);

        int echos = receivedEchos.get(gm.getNonce()).size();

        if (echos >= (n + t + 1) / 2 && !sentReadies.contains(gm.getNonce())) {
            GexMessage ready = new GexMessage(gm.getMessage(), "rd", gm.getNonce());
            sendToParties(ready);
            sentReadies.add(gm.getNonce());
        }
    }

    private void checkReady(GexMessage gm) throws IOException, NoSuchAlgorithmException {
        if (!receivedReadies.containsKey(gm.getNonce())) {
            receivedReadies.put(gm.getNonce(), new ArrayList<>());
            return;
        }

        receivedReadies.get(gm.getNonce()).add(gm);
        int readies = receivedReadies.get(gm.getNonce()).size();

        if (readies >= t + 1 && !sentReadies.contains(gm.getNonce())) {
            GexMessage ready = new GexMessage(gm.getMessage(), "rd", gm.getNonce());
            sendToParties(ready);
            sentReadies.add(gm.getNonce());
        }

        if (readies >= 2 * t + 1) {
            committedMessages.add(gm);
        }
    }


    private void sendToParties(GexMessage gm) throws IOException, NoSuchAlgorithmException {
        for (Party p : parties) {
            sendMessage(gm, p.getAddress(), p.getPort());
        }
    }

    private void checkTime() {
        if (committedMessages.size() == TEST_AMOUNT_MESSAGES) {

            Instant startTime = Instant.parse(committedMessages.get(0).getSendTime());
            Instant finishTime = Instant.now();

            Duration timeElapsed = Duration.between(startTime, finishTime);


            System.out.println(String.format("Party %s : %s messages elapsed time: %s ", this,
                    TEST_AMOUNT_MESSAGES, timeElapsed));
        }
    }

    public String getPartyId() {
        return partyId;
    }

    private void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public static Party createParty(ReliablePartyConfig config) throws SocketException, UnknownHostException {

        Party party = new Party(config.getAddress(), config.getPort(), config.getId());


        for (Map partyConf : config.getParties()) {
            String addr = (String) partyConf.get("address");
            Integer p = (Integer) partyConf.get("port");
            String id = (String) partyConf.get("id");
            party.addParty(remoteParty(addr, p, id));
        }

        return party;
    }


}
