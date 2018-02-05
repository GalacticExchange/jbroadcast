import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import udp.FragmentProto;

public class ProtoBufTest {

    public static void main(String[] args) throws InvalidProtocolBufferException {
//        Builder builder = FragmentProto.MessagePacket.newBuilder();
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

//        String str1 = "zz";
        //String str1 = "dissynqbfvxggjebymocgqyxvbfvyjqnvpyrbetyyssbjiiueqybvnbbdbjikkhsttcyifgxhmxummvwzghxyxcmuvztysrnejwfwbgqqupakaxsyreqpadiklnnyexlxztttfvgyustufylymmpiwunuhdauagujpjxasjgsiazbjukonlbgcmkiejkdtjfioczcdiqiwxufrhhvaagqttcuigamexofwzwnvfpgjrimpxpygyyeaqftouazbgkcpxolosovdggsaxrvoeavfudspgudwuzjgjnpygepqjgjjfsrzodflgvfrtptsprpfkakptigxmlhdgjaqplbkbhobcthyxhpgxzbbbvcvpwvcljndpsvvgvewcvldrpkcoeudstkexbguhamxoinfcjatmyspafrgpelxcxftnaorngosfjyemrzldykxqatxcnahkxrtiyfmxqlrezatrrbabchfdtfgxcnehzskqbjirubvtmebpncntuktnnxhwdsejuibxibtvnycgclihhpfwnbnyyaehhjujlacdkwxvudedmkczffruiyjfozrrgrpcsdbkkcftbrpmqbfhgwyoknyqpxwtunlmmebveacagubkpbewjlpezqigzwbxrdfyrlultewpqdiugpugeoichncnyvwzdovpgzcwmfvysovdgotjpyyawjbaerlpwydmiqnqzwmflyhxgcwvruiouzueiqdbcdfyhwzizoslthjjjizbwwrqdheykbvajswfkhqbgpleehsvtzjrnbyshfxeltgwimqxbaevywacztwaeadgwxlbdhrabvjntngrlsjjezysihwztcfbcacbnahjdagfdadizpcchfvxcrmjysvqllpryrpgsgtnurkxwpoayzrtriuwymcevxyguptvffioorgqlsabmmseohsnigyukbifqckbrmmlzmjiaqalfcsqxixkwzpnxiczpylyuwwqtvuolcixzmuioufjyddonmivaseip";
        String str1 = "1dheykbvajswfkhqbgpleehsvtzjrnbyshfxeltgwimqxbaevywacztwaeadgwxlbdhrabvjntngrlsjjezysihwztcfbcacbnahjdagfdadizpcchfvxcrmjysvqllp";
        String cmd1 = "zz";
        String nonce1 = "zz";

        FragmentProto.GexMessage.Builder frameBuilder = FragmentProto.GexMessage.newBuilder();
        frameBuilder.setMessage(str1);
//        frameBuilder.addSigns("123213");
//        frameBuilder.addSigns("567123");
        FragmentProto.GexMessage gm = frameBuilder.build();
        gm.toByteString();

        FragmentProto.Fragment.Builder builder = FragmentProto.Fragment.newBuilder();


//        ByteString bStr1 = ByteString.copyFrom(str1.getBytes());
        ByteString bCmd1 = ByteString.copyFrom(cmd1.getBytes());
        ByteString bNonce1 = ByteString.copyFrom(nonce1.getBytes());

        builder.setVersion(1); // (1+4)         5
        builder.setIndex(1); // (1+4)           5
        builder.setAmount(1); // (1+4)          5
        builder.setLengthTotal(1); // (1+4)     5
//        builder.setCommand(bCmd1); // (2 + 2)   4
        builder.setNonce(bNonce1); // (2 + 2)   4
        builder.setData(ByteString.copyFrom(str1.getBytes())); // (2 + 1024) 1026
        // total bytes = 32

        FragmentProto.Fragment fm = builder.build();

        System.out.println("Total byte[] length: " + fm.toByteArray().length + "\n");
        int dataKeyLength = fm.toByteArray().length - 28 - str1.getBytes().length;
        System.out.println("Data value length:" + str1.getBytes().length + " Data key length:" + dataKeyLength + "\n");
        System.out.println(fm);

        int x = fm.getData().toByteArray().length +
                fm.getNonce().toByteArray().length;

        System.out.println(fm.toByteArray().length - x);
        System.out.println(fm.toByteString());


//        byte[] data = fm.getData().toByteArray();
//        byte[] total = new byte[100];
//        ByteBuffer buffer = ByteBuffer.wrap(total);
//        buffer.put(data);
//
//        FragmentProto.GexMessage gm2 = FragmentProto.GexMessage.parseFrom(total);
//
//        System.out.println(gm2);
//        for (String s : gm.getSignsList()) {
//            System.out.println(s);
//        }

    }
}
