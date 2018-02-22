package reliable.multithreaded;

import com.google.protobuf.InvalidProtocolBufferException;
import udp.FragmentPacket;
import udp.GexMessage;

import java.util.*;

public abstract class Messenger {
    protected Map<String, List<FragmentPacket>> receivedFragments;


    public Messenger(){
        receivedFragments = new HashMap<>();
    }

    public void processFragment(FragmentPacket fp) throws InvalidProtocolBufferException {
        String nonce = fp.getNonce();

        if (!receivedFragments.containsKey(nonce)) {
//            receivedFragments.put(nonce, new ArrayList<>());
            receivedFragments.put(nonce, new ArrayList<>());
        }

        //todo kludge
        if(receivedFragments.get(nonce).isEmpty()){
            receivedFragments.get(nonce).add(fp);
        }

        if (isLastPacket(fp)) {
            GexMessage gm = assembleMessage(fp.getNonce());
            processMessage(gm, fp.getAddress(), fp.getPort());
        }
    }

    private GexMessage assembleMessage(String nonce) throws InvalidProtocolBufferException {

        FragmentPacket[] packets = new FragmentPacket[receivedFragments.get(nonce).size()];
        receivedFragments.get(nonce).toArray(packets);
        GexMessage gm = FragmentPacket.assembleMessage(packets);
        receivedFragments.remove(nonce);
        return gm;
    }


    private boolean isLastPacket(FragmentPacket fp) {
        // TODO the order could not be guaranteed.. check HashMap[nonce] length or smth...
        return fp.getIndex() + 1 == fp.getAmount();
    }

    public abstract void processMessage(GexMessage gm, String address, int port);
}
