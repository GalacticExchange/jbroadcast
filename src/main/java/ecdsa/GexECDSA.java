package ecdsa;

import utils.FileUtils;

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
        keyGen.initialize(256, random);
        pair = keyGen.generateKeyPair();
    }

    /**
     * Parses existing key pair.
     */
    public GexECDSA(String privateKeyPath, String publicKeyPath) throws InvalidKeySpecException,
            NoSuchAlgorithmException, IOException {
        readKeys(privateKeyPath, publicKeyPath);
    }

    private void readKeys(String privateKeyPath, String publicKeyPath) throws InvalidKeySpecException,
            NoSuchAlgorithmException, IOException {

//        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PrivateKey privateKey = (PrivateKey) readKey(privateKeyPath, PrivateKey.class);
        PublicKey pubKey = (PublicKey) readKey(publicKeyPath, PublicKey.class);

        this.pair = new KeyPair(pubKey, privateKey);
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

    public static PublicKey readPublicKey(String pubKeyPath, KeyFactory keyFactory) throws InvalidKeySpecException, IOException {
        return parsePublicKey(FileUtils.readFileString(pubKeyPath), keyFactory);
    }

    public static PrivateKey readPrivateKey(String privateKeyPath, KeyFactory keyFactory) throws InvalidKeySpecException, IOException {
        byte[] privateKeyBytes = Base64.getDecoder().decode(FileUtils.readFileString(privateKeyPath));
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return keyFactory.generatePrivate(privateKeySpec);
    }

    public static Key readKey(String path, Class keyClass) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        if (keyClass.equals(PublicKey.class)) {
            return readPublicKey(path, keyFactory);
        } else if (keyClass.equals(PrivateKey.class)) {
            return readPrivateKey(path, keyFactory);
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

    public static PublicKey parsePublicKey(String publicKey, KeyFactory keyFactory) throws InvalidKeySpecException {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(pubKeySpec);
    }


}
