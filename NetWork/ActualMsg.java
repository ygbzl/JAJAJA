package NetWork;


import java.io.IOException;
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

        public int getTypeValue(MsgType msgType){
            return msgType.val;
        }
        byte getMsgType() {
            return (byte) val;
        }

        public boolean isType(MsgType msgType){
            if(this.val == msgType.val){
                return true;
            }else{
                return false;
            }
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
        this.type = msgType;
        this.msgLength = new byte[]{0, 0, 0, 1};
        this.msgPaylod = null;
        this.msgType=msgType.getMsgType();

    }

    ActualMsg(MsgType msgType, int index) {
        //for have message and request message
            this.msgLength = new byte[]{0, 0, 0, 1};
            this.type = msgType;
            this.msgType = type.getMsgType();
            this.msgPaylod = ConstantMethod.intToBytes(index);

    }

    ActualMsg(FilePiece piece) {
        //for piece message
        this.type=MsgType.PIECE;
        this.msgType=type.getMsgType();
        this.msgLength = ConstantMethod.intToBytes(4 + piece.getPiecesArray().length);
        this.msgPaylod = piece.getPiecesArray();

    }

    byte[] msgLength;
    byte[] msgPaylod;
    byte msgType;
    MsgType type;

    /**
     * send actual msg
     */
    public void sendActualMsg(OutputStream out) throws IOException{
        byte[] toSend = ConstantMethod.mergeBytes(this.msgLength, new byte[]{this.msgType});
        toSend = ConstantMethod.mergeBytes(toSend, this.msgPaylod);
        out.write(toSend);
        //todo
    }

    public static ActualMsg readActualMsg(InputStream in) throws IOException {

        //todo
        byte[] msgLength = new byte[4];
        byte[] msgType_temp=new byte[1];
        in.read(msgLength);
        in.read(msgType_temp);
        byte msgType = msgType_temp[0];
        int length = ConstantMethod.bytesToInt(msgLength);
        byte[] msgPayLoad = new byte[length];
        in.read(msgPayLoad);
        return new ActualMsg(msgLength, msgType, msgPayLoad);
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
