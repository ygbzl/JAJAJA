package NetWork;

/**
 * Created by zhupd on 2/18/2017.
 */
public class ActualMsg {

     enum msgType {
        CHOKE((byte)0),
        UNCHOKE((byte)1),
        INTERESTED((byte)2),
        NOTINTERESTED((byte)3),
        HAVE((byte)4),
        BITFIELD((byte)5),
        REQUEST((byte)6),
        PIECE((byte)7);


        byte val=(byte)-1;

         msgType(byte val) {
            this.val=val;
        }
    }

    ActualMsg(byte[] msgLength, byte msgType, byte[] msgPaylod) {
        this.msgLength=msgLength;
        this.msgType=msgType;
        this.msgPaylod = msgPaylod;
        //split msg[]

    }

    byte[] msgLength;
    byte[] msgPaylod;
    byte msgType;
    msgType type;

    /**
     * send actual msg
     */
    void sendActualMsg() {

        //todo
    }

    void readActualMsg() {
        //todo
    }

    int getIndex() {
        byte index[] = new byte[4];
        for(int i=0;i<4;i++) {
            index[i] = msgPaylod[i];
        }
        return ConstantMethod.bytesToInt(index);
    }

    msgType getType() {
        //todo
        return type;
    }

}
