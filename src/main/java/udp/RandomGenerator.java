package udp;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RandomGenerator {

    private static SecureRandom random = new SecureRandom();
    private static final String AB = "0123456789abcdefghijklmnopqrstuvwxyz";


    public static String generateString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(random.nextInt(AB.length())));
        return sb.toString();
    }

    public static byte[] generateByteArray(int len) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[len];
        random.nextBytes(bytes);
        return bytes;
    }

}
