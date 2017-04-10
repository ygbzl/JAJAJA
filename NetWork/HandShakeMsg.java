package NetWork;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.lang.System.in;

/**
 * Created by zhupd on 2/18/2017.
 */
public class HandShakeMsg {
    final static byte[] HEADER="P2PFILESHARINGPROJ".getBytes();
    final static byte[] ZEROBIT={0,0,0,0,0,0,0,0,0,0};
    int peerID;


    HandShakeMsg(int ID) {
        this.peerID = ID;

    }


    /**
     * get a handshake message and return as a HandShakeMsg instance
     * which contains the guestID
     *
     * @param io
     * @return
     * @throws IOException
     */
    public int readMsg(InputStream io) throws IOException {
        byte[] header = new byte[18];
        byte[] zeroBit = new byte[10];
        byte[] peerID = new byte[4];
        io.read(header);
        io.read(zeroBit);
        io.read(peerID);

        if (!header.equals(HEADER) || !zeroBit.equals(ZEROBIT)) {
            throw new IOException("not a handshake message");
        }

        return ConstantMethod.bytesToInt(peerID);

    } //read handshake


    /**
     * send to guest a handshakeMsg with local ID
     * @param out
     * @throws IOException
     */
    void sendMsg(OutputStream out) throws IOException{
        byte[] byteID = new byte[4];
        byteID = ConstantMethod.intToBytes(peerID);
        byte[] msg = ConstantMethod.mergeBytes(HEADER, ZEROBIT);
        msg = ConstantMethod.mergeBytes(msg, byteID);
        out.write(msg);
        out.flush();
    } //send handshake

}
