package ecdsa;

import utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class GexECDSA {

    private static final String ALGORITHM = "EC";
    private static final String SIGN_ALGORITHM = "SHA1withECDSA";


    private KeyPair pair;

    /**
     * Generates new KeyPair
     */
    public GexECDSA() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        keyGen.initialize(160, random);
        pair = keyGen.generateKeyPair();
    }

    /**
     * Parses existing key pair from path.
     */
    public GexECDSA(File privateKeyFile, File publicKeyFile) throws InvalidKeySpecException,
            NoSuchAlgorithmException, IOException {
        readKeys(privateKeyFile, publicKeyFile);
    }

    /**
     *
     */
    public GexECDSA(String publicKey, String privateKey) throws InvalidKeySpecException, NoSuchAlgorithmException {

        PublicKey pubKey = parsePublicKey(publicKey);
        PrivateKey privKey = parsePrivateKey(privateKey);

        pair = new KeyPair(pubKey, privKey);
    }


    private void readKeys(File privateKey, File publicKey) throws InvalidKeySpecException,
            NoSuchAlgorithmException, IOException {

        PrivateKey privKey = (PrivateKey) readKey(privateKey, PrivateKey.class);
        PublicKey pubKey = (PublicKey) readKey(publicKey, PublicKey.class);

        pair = new KeyPair(pubKey, privKey);
    }


    /**
     * Saves keys as Base64 encoded strings
     */
    public void saveKeys(String privateKeyPath, String publicKeyPath) throws IOException {
        String pubKeyString = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
        String privKeyString = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());

        FileUtils.writeFile(publicKeyPath, pubKeyString.getBytes());
        FileUtils.writeFile(privateKeyPath, privKeyString.getBytes());

    }

    public static PublicKey readPublicKey(File pubKey) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
        return parsePublicKey(FileUtils.readFileString(pubKey));
    }

    public static PrivateKey readPrivateKey(File privateKey) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
        return parsePrivateKey(FileUtils.readFileString(privateKey));
    }

    public static Key readKey(File file, Class keyClass) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        if (keyClass.equals(PublicKey.class)) {
            return readPublicKey(file);
        } else if (keyClass.equals(PrivateKey.class)) {
            return readPrivateKey(file);
        } else {
            throw new InvalidClassException("Invalid Key Class: " + keyClass);
        }
    }

    public String sign(String msg) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        byte[] sig = sign(msg.getBytes());
        return Base64.getEncoder().encodeToString(sig);
    }

    public boolean verifySign(String msg, String sig, PublicKey publicKey) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        byte[] bSig = Base64.getDecoder().decode(sig);

        return verifySign(msg.getBytes(), bSig, publicKey);
    }

    public byte[] sign(byte[] msg) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {

        Signature sig = Signature.getInstance(SIGN_ALGORITHM);

        sig.initSign(pair.getPrivate());
        sig.update(msg);

        return sig.sign();
    }

    public boolean verifySign(byte[] msg, byte[] sign, PublicKey publicKey) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance(SIGN_ALGORITHM);
        sig.initVerify(publicKey);
        sig.update(msg);

        return sig.verify(sign);
    }

    public PublicKey getPublic() {
        return pair.getPublic();
    }

    public PrivateKey getPrivate() {
        return pair.getPrivate();
    }

    public static PublicKey parsePublicKey(String publicKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(pubKeySpec);
    }

    public static PrivateKey parsePrivateKey(String privateKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey);
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return keyFactory.generatePrivate(privateKeySpec);
    }


}
