package udp;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

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
//        SecureRandom.getInstanceStrong().nextBytes(bytes); // slow: collects entropy
        random.nextBytes(bytes);
        return bytes;
    }


    public static void main(String[] args) throws NoSuchAlgorithmException {
//        System.out.println(generateString(4));
        System.out.println(Arrays.hashCode(generateByteArray(4)));
    }
}
