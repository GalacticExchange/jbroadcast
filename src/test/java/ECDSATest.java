import ecdsa.GexECDSA;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class ECDSATest {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        GexECDSA gexECDSA = new GexECDSA();
        String str = "Hello world!";
        byte[] strBytes = str.getBytes("UTF-8");

        byte[] sign = gexECDSA.sign(strBytes);

        boolean verified = gexECDSA.verifySign(strBytes, sign, gexECDSA.getPublic());
        System.out.println("Verified: " + verified);

        gexECDSA.saveKeys("private", "public");

        GexECDSA parsed = new GexECDSA("private", "public");
        boolean verifiedParsed = parsed.verifySign(str.getBytes("UTF-8"), sign, parsed.getPublic());
        System.out.println("Verified2: " + verifiedParsed);
//        System.out.println("Parsed public key: " + ByteUtils.byteArrToString(parsed.getPrivate().getEncoded()));

        String strKey = Base64.getEncoder().encodeToString(parsed.getPublic().getEncoded());

        System.out.println("Parsed public key: " + strKey);
    }
}
