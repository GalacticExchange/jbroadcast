package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static byte[] readFile(String filePath) throws IOException {
        FileInputStream keyfis = new FileInputStream(filePath);
        byte[] encKey = new byte[keyfis.available()];
        keyfis.read(encKey);

        keyfis.close();

        return encKey;
    }

    public static String readFileString(File file) throws IOException {
        return ByteUtils.byteArrToString(readFile(file.getPath()));
    }

    public static void writeFile(String filePath, byte[] data) throws IOException {
        FileOutputStream keyfos = new FileOutputStream(filePath);
        keyfos.write(data);
        keyfos.close();
    }
}
