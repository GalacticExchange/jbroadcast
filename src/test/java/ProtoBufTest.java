import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import udp.MessagePacketProto;
import udp.MessagePacketProto.Frame;


public class ProtoBufTest {

    public static void main(String[] args) throws InvalidProtocolBufferException {
//        Builder builder = MessagePacketProto.MessagePacket.newBuilder();
//        String val = "Hello protobuf!";
//        ByteString value = ByteString.copyFrom(val.getBytes());
//
//        builder.setIndex(value);
//        builder.setAmount(value);
//        builder.setCommand(value);
//
//        MessagePacket mp = builder.build();
//
//        System.out.println(mp);
//
//        System.out.println(mp.toByteArray());
//
//        System.out.println(mp.toByteArray().length);
//
//        MessagePacket mp2 = MessagePacket.parseFrom(mp.toByteArray());
//        System.out.println(mp2);
//
//        try {
//            // write
//            FileOutputStream output = new FileOutputStream("protobuf.ser");
//            mp.writeTo(output);
//            output.close();
//
//            } catch (IOException e) {
//            e.printStackTrace();
//        }

        String str1 = "zz";
//        String str1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."; // 56
        String cmd1 = "zz";
        String nonce1 = "zz";

        MessagePacketProto.GexMessage.Builder frameBuilder = MessagePacketProto.GexMessage.newBuilder();
        frameBuilder.setMessage(str1);
        frameBuilder.addSigns("123213");
        frameBuilder.addSigns("567123");
        MessagePacketProto.GexMessage gm = frameBuilder.build();
        gm.toByteString();

        MessagePacketProto.Frame.Builder builder = MessagePacketProto.Frame.newBuilder();


//        ByteString bStr1 = ByteString.copyFrom(str1.getBytes());
        ByteString bCmd1 = ByteString.copyFrom(cmd1.getBytes());
        ByteString bNonce1 = ByteString.copyFrom(nonce1.getBytes());

        builder.setVersion(1); // 7
        builder.setIndex(1); // 7
        builder.setAmount(1); // 7
        builder.setCommand(bCmd1); // 2
        builder.setNonce(bNonce1); // 2
        builder.setData(gm.toByteString()); // 2
        // total bytes = 22
        // total bytes = 74 (75)

        Frame fm = builder.build();

        System.out.println("Total byte[] length: " + fm.toByteArray().length + "\n");
        System.out.println(fm);


        MessagePacketProto.GexMessage gm2 = MessagePacketProto.GexMessage.parseFrom(fm.getData().toByteArray());

        System.out.println(gm2);
        for (String s : gm.getSignsList()) {
            System.out.println(s);
        }

    }
}
