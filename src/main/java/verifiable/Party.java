package verifiable;

import config.VerifiablePartyConfig;
import ecdsa.GexECDSA;
import udp.Communicator;
import udp.GexMessage;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Party extends Communicator {

    private static final String PUBLIC_KEY_NAME = "publicKey";
    private static final String PRIVATE_KEY_NAME = "privateKey";
    private static final int t = 1;
    private static final int n = 5;
    public static final int TEST_AMOUNT_MESSAGES = 1000;

    private GexECDSA gexECDSA;
    private HashMap<String, PublicKey> publicKeys;
    private ArrayList<GexMessage> committedMessages;
    private String partyId;

    // Todo: party.yml constructor?

    /**
     * Creates new ECDSA keys
     */
    public Party(String addr, int port, String partyId) throws SocketException, UnknownHostException, NoSuchAlgorithmException {
        super(addr, port);

        this.partyId = partyId;
        committedMessages = new ArrayList<>();
        gexECDSA = new GexECDSA();
        publicKeys = new HashMap<>();
    }

    /**
     * Parses keys from directory
     */
    public Party(String keysDir, String addr, int port, String partyId) throws IOException,
            InvalidKeySpecException, NoSuchAlgorithmException {
        super(addr, port);

        String privateKeyPath = Paths.get(keysDir, PRIVATE_KEY_NAME).toString();
        String publicKeyPath = Paths.get(keysDir, PUBLIC_KEY_NAME).toString();

        this.partyId = partyId;

        committedMessages = new ArrayList<>();
        gexECDSA = new GexECDSA(new File(privateKeyPath), new File(publicKeyPath));
        publicKeys = new HashMap<>();
    }


    /**
     * Parses from strings
     */
    public Party(String publicKey, String privateKey, String addr, int port, String partyId) throws InvalidKeySpecException,
            NoSuchAlgorithmException, SocketException, UnknownHostException {
        super(addr, port);
        this.partyId = partyId;
        committedMessages = new ArrayList<>();
        gexECDSA = new GexECDSA(publicKey, privateKey);
        publicKeys = new HashMap<>();
    }

    public void addPublicKeyToList(String id, File publicKeyPath) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        PublicKey publicKey = (PublicKey) GexECDSA.readKey(new File(publicKeyPath.getPath()), PublicKey.class);
        publicKeys.put(id, publicKey);
    }

    public void addPublicKeyToList(String id, String pubKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        PublicKey publicKey = GexECDSA.parsePublicKey(pubKey);
        publicKeys.put(id, publicKey);

    }

    public void saveKeys(String keysDir) throws IOException {
        String privateKeyPath = Paths.get(keysDir, PRIVATE_KEY_NAME).toString();
        String publicKeyPath = Paths.get(keysDir, PUBLIC_KEY_NAME).toString();
        gexECDSA.saveKeys(privateKeyPath, publicKeyPath);
    }


    @Override
    public void processMessage(GexMessage gm, String address, int port) {
        try {

            if (gm.getCommand().equals("sg")) {


                String sig = gexECDSA.sign(gm.getMessage());

                HashMap<String, String> map = new HashMap<>();
                map.put(partyId, sig);

                GexMessage singedMessage = new GexMessage(gm.getMessage(), "sg",
                        gm.getNonce(), gm.getSendTime(), map);
                sendMessage(singedMessage, address, port);


            } else if (gm.getCommand().equals("ch")) {

//                System.out.println("Party got check message:\n" + gm);

                Map<String, String> signs = gm.getSigns();
                int verifiedSigns = 0;

                for (String key : signs.keySet()) {
                    if (gexECDSA.verifySign(gm.getMessage(), signs.get(key), publicKeys.get(key))) {
                        verifiedSigns++;

                        if (verifiedSigns >= (n + t + 1) / 2) {
                            break;
                        }

                    }
                }


                if (verifiedSigns >= (n + t + 1) / 2) {

                    committedMessages.add(gm);

                    if (committedMessages.size() == TEST_AMOUNT_MESSAGES) {

                        Instant startTime = Instant.parse(committedMessages.get(0).getSendTime());
                        Instant finishTime = Instant.now();

                        Duration timeElapsed = Duration.between(startTime, finishTime);


                        System.out.println(String.format("Party %s : %s messages elapsed time: %s ", this,
                                TEST_AMOUNT_MESSAGES, timeElapsed));
                    }
                }
            }

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Party createParty(VerifiablePartyConfig config) throws NoSuchAlgorithmException, IOException,
            InvalidKeySpecException {

        Party party = new Party(config.getPublicKey(), config.getPrivateKey(),
                config.getAddress(), config.getPort(), config.getId());


        for (Map partyConf : config.getParties()) {
            String id = (String) partyConf.get("id");
            String pubKey = (String) partyConf.get("public_key");
            party.addPublicKeyToList(id, pubKey);
        }

        return party;
    }


}
