import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class PartySend {

    public static void main(String[] args) {
        try {

            Party party = new Party("localhost", 1400);
            party.sendMessage("TestMessage", "localhost", 1401);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
