package udp;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BatchMessages {

    private FragmentProto.BatchMessagesProto batchMessagesProto;
    private List<SkaleMessage> messages;

    public BatchMessages(ArrayList<SkaleMessage> messages) {
        if (messages.size() > 5) {
            throw new IllegalArgumentException("Messages array is too big");
        }

        FragmentProto.BatchMessagesProto.Builder builder = FragmentProto.BatchMessagesProto.newBuilder();
        ArrayList<FragmentProto.SkaleMessage> arr = new ArrayList<>(getSkaleProtoList(messages));
        builder.addAllMessages(arr);
        batchMessagesProto = builder.build();
        setMessages(messages);
    }

    private BatchMessages() {

    }

    public List<SkaleMessage> getMessages() {
        return messages;
    }

    private List<FragmentProto.SkaleMessage> getSkaleProtoList(List<SkaleMessage> list) {
//        ArrayList<FragmentProto.SkaleMessage> messages = new ArrayList<>();
//        list.forEach((m) -> messages.add(m.getProtoObject()));
//
//        return messages;
        return list.stream().map(SkaleMessage::getProtoObject).collect(Collectors.toList());
    }

    private List<SkaleMessage> getSkaleList(List<FragmentProto.SkaleMessage> list) {
        return list.stream().map(SkaleMessage::new).collect(Collectors.toList());
    }

    private void initFields(FragmentProto.BatchMessagesProto batchMessagesProto) {
        setBatchMessagesProto(batchMessagesProto);
        setMessages(getSkaleList(batchMessagesProto.getMessagesList()));
    }

    private void setMessages(List<SkaleMessage> messages) {
        this.messages = messages;
    }

    private void setBatchMessagesProto(FragmentProto.BatchMessagesProto batchMessagesProto) {
        this.batchMessagesProto = batchMessagesProto;
    }


    public static BatchMessages parse(byte[] bytes) throws InvalidProtocolBufferException {
        FragmentProto.BatchMessagesProto batchMessagesProto = FragmentProto.BatchMessagesProto.parseFrom(bytes);
        BatchMessages batchMessages = new BatchMessages();
        batchMessages.initFields(batchMessagesProto);
        return batchMessages;
    }

//    public static BatchMessages packetToBatchMessages(Packet packet) throws InvalidProtocolBufferException {
//        BatchMessages bm = new BatchMessages();
//        bm.initFields(FragmentProto.BatchMessagesProto.parseFrom(packet.getRawData()));
//        return bm;
//    }

    public byte[] toByteArray() {
        return batchMessagesProto.toByteArray();
    }
}
