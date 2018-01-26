package ecdsa;


import utils.ByteUtils;
import utils.FileUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class GexECDSA {

    public static final String ALGORITHM = "EC";
    public static final String SIGN_ALGORITHM = "SHA1withECDSA";

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

    public GexECDSA(String privateKeyPath, String publicKeyPath) throws InvalidKeySpecException,
            NoSuchAlgorithmException, IOException {
        readKeys(privateKeyPath, publicKeyPath);
    }


    public KeyPair getPair() {
        return pair;
    }

    public void readKeys(String privateKeyPath, String publicKeyPath) throws InvalidKeySpecException,
            NoSuchAlgorithmException, IOException {

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PrivateKey privateKey = readPrivateKey(privateKeyPath, keyFactory);
        PublicKey pubKey = readPublicKey(publicKeyPath, keyFactory);

        this.pair = new KeyPair(pubKey, privateKey);
    }

    public void saveKeys(String privateKeyPath, String publicKeyPath) throws IOException {

//        FileUtils.writeFile(privateKeyPath, pair.getPrivate().getEncoded());
//        FileUtils.writeFile(publicKeyPath, pair.getPublic().getEncoded());

//        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
//                pair.getPrivate().getEncoded());
//        FileUtils.writeFile(privateKeyPath, pkcs8EncodedKeySpec.getEncoded());
//
//
//        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
//                pair.getPublic().getEncoded());
//        FileUtils.writeFile(publicKeyPath, x509EncodedKeySpec.getEncoded());

        String pubKeyString = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
        String privKeyString = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());

        FileUtils.writeFile(privateKeyPath, pubKeyString.getBytes());
        FileUtils.writeFile(publicKeyPath, privKeyString.getBytes());
    }

    private PublicKey readPublicKey(String pubKeyPath, KeyFactory keyFactory) throws InvalidKeySpecException, IOException {
//        byte[] publicKeyBytes = FileUtils.readFile(pubKeyPath);
        byte[] publicKeyBytes = Base64.getDecoder().decode(FileUtils.readFileString(pubKeyPath));
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(pubKeySpec);
    }

    private PrivateKey readPrivateKey(String privateKeyPath, KeyFactory keyFactory) throws InvalidKeySpecException, IOException {
//        byte[] privateKeyBytes = FileUtils.readFile(privateKeyPath);
        byte[] privateKeyBytes = Base64.getDecoder().decode(FileUtils.readFileString(privateKeyPath));
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return keyFactory.generatePrivate(privateKeySpec);
    }


//    public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        PrivateKey privateKey = pair.getPrivate();
//        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
//
////        ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec(ALGORITHM);
//        ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec("secp192r1");
//
//        ECDomainParameters ecDomainParameters =
//                new ECDomainParameters(
//                        parameterSpec.getCurve(),
//                        parameterSpec.getG(),
//                parameterSpec.getN(), parameterSpec.getH(), parameterSpec.getSeed());
//
//        byte[] privateKeyBytes = privateKey.getEncoded();
//
//        ECPoint ecPoint = ecDomainParameters.getG().multiply(new BigInteger(privateKeyBytes));
//
//
//        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(ecPoint.getEncoded());
//        return keyFactory.generatePublic(pubKeySpec);
//
//    }

    public byte[] sign(String msg) throws UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {

        byte[] bMsg = msg.getBytes("UTF-8");
        Signature sig = Signature.getInstance(SIGN_ALGORITHM);

        sig.initSign(pair.getPrivate());
        sig.update(bMsg);

        return sig.sign();
    }

    public boolean verifySign(byte[] msg, byte[] sign, PublicKey publicKey) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance(SIGN_ALGORITHM);
        sig.initVerify(publicKey);
        sig.update(msg);

        return sig.verify(sign);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        GexECDSA gexECDSA = new GexECDSA();
        String str = "Hello world!";

        byte[] sign = gexECDSA.sign(str);

        boolean verified = gexECDSA.verifySign(str.getBytes("UTF-8"), sign, gexECDSA.getPair().getPublic());
        System.out.println("Verified: " + verified);
        gexECDSA.saveKeys("private", "public");

        GexECDSA parsed = new GexECDSA("private", "public");
        boolean verifiedParsed = parsed.verifySign(str.getBytes("UTF-8"), sign, parsed.getPair().getPublic());
        System.out.println("Verified2: " + verifiedParsed);
//        System.out.println("Parsed public key: " + ByteUtils.byteArrToString(parsed.getPair().getPrivate().getEncoded()));

        String strKey = Base64.getEncoder().encodeToString(parsed.getPair().getPublic().getEncoded());

        System.out.println("Parsed public key: " + strKey);

    }


}
