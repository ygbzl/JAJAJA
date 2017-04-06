package NetWork;


import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhupd on 2/18/2017.
 */
public class ActualMsg {

    enum MsgType {
        CHOKE(0),
        UNCHOKE(1),
        INTERESTED(2),
        NOTINTERESTED(3),
        HAVE(4),
        BITFIELD(5),
        REQUEST(6),
        PIECE(7);


        final int val;

        MsgType(int val) {

            this.val = val;
        }

        byte getMsgType() {
            return (byte) val;
        }
    }

    ActualMsg(){

    }

    ActualMsg(byte[] msgLength, byte msgType, byte[] msgPaylod) {
        this.msgLength = msgLength;
        this.msgType = msgType;
        this.msgPaylod = msgPaylod;
        //split msg[]
    }

    ActualMsg(BitField bitField) {
        this.msgLength = ConstantMethod.intToBytes(bitField.getLength());
        type = MsgType.BITFIELD;
        msgType = type.getMsgType();
        msgPaylod = bitField.getData();
    }

    ActualMsg(MsgType msgType) {
        //for no payload message
    }

    ActualMsg(MsgType msgType, int index) {
        //for have message and request message
    }

    ActualMsg(FilePiece piece) {
        //for piece message
    }

    byte[] msgLength;
    byte[] msgPaylod;
    byte msgType;
    MsgType type;

    /**
     * send actual msg
     */
    void sendActualMsg(OutputStream out) {

        //todo
    }

    void readActualMsg(InputStream in) {

        //todo
    }

    int getIndex() {
        byte index[] = new byte[4];
        for (int i = 0; i < 4; i++) {
            index[i] = msgPaylod[i];
        }
        return ConstantMethod.bytesToInt(index);
    }

    MsgType getType() {
        //todo
        return type;
    }

}
