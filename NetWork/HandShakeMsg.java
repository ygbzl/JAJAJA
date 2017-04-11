package NetWork;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.in;

/**
 * Created by zhupd on 2/18/2017.
 */
public class HandShakeMsg {
    private final static byte[] HEADER="P2PFILESHARINGPROJ".getBytes();
    private final static byte[] ZEROBIT={0,0,0,0,0,0,0,0,0,0};
    private int peerID;


    HandShakeMsg(int ID) {
        this.peerID = ID;

    }


    /**
     * get a handshake message and return as a HandShakeMsg instance
     * which contains the guestID
     *
     * @param s
     * @return
     * @throws IOException
     */
    public int readMsg(Socket s) throws IOException {
        //Scanner in = new Scanner(io);
        InputStream in = s.getInputStream();

        byte[] header = new byte[18];
        byte[] zeroBit = new byte[10];
        byte[] peerID = new byte[4];

        int bytesReceived;
        int totalBytesReceived = 0;

        while (totalBytesReceived < 18) {
            bytesReceived = in.read(header, totalBytesReceived, 18- totalBytesReceived);
            totalBytesReceived += bytesReceived;
        }

        totalBytesReceived = 0;
        while (totalBytesReceived < 10){
            bytesReceived = in.read(zeroBit, totalBytesReceived, 10 - totalBytesReceived);
            totalBytesReceived += bytesReceived;
        }

        totalBytesReceived = 0;
        while (totalBytesReceived < 4) {
            bytesReceived = in.read(peerID, totalBytesReceived, 4 - totalBytesReceived);
            totalBytesReceived += bytesReceived;
        }

        /*io.read(header);
        io.read(zeroBit);
        io.read(peerID);*/


        if (!ConstantMethod.byteArrayComparer(HEADER, header) || !ConstantMethod.byteArrayComparer(ZEROBIT,zeroBit)) {
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
        //System.out.println("msg sent" + msg);
    } //send handshake

}
