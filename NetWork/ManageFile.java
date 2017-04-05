package NetWork;

/**
 * Created by zhupd on 2/20/2017.
 */
import java.io.*;

/*
 * manage file
 * get file name
 * read file
 * write file to
 */


public class ManageFile {
    public static Config config;
    private static RandomAccessFile file;
    //private ActualMsg actualmsg;

    /**
     * Establish the subdirectory according to the self peer ID
     *
     * @param config
     * @param myID
     * @throws FileNotFoundException
     */

    public ManageFile(Config config, int myID) throws FileNotFoundException{
        this.config = config;

        String directory = "peer_" + myID + "/";

        File dir = new File(directory);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        file = new RandomAccessFile(directory + config.FileName, "rw");

<<<<<<< HEAD
    }

=======
    public synchronized  void readMsg()
>>>>>>> 7e2e0acad789b9a13e3f5111155beac0ea9e4893

    /**
     * fetch file piece according to the index
     * packed into a piece message and transform into a byte stream
     * and return
     * @param index
     * @throws IOException
     * @return byte[]
     */

<<<<<<< HEAD
    public static byte[] writePieceMsg(byte[] index) throws IOException {
=======
    public synchronized void writeMsg(OutputStream otp,byte[] index, Config config) throws IOException {
>>>>>>> 7e2e0acad789b9a13e3f5111155beac0ea9e4893
        byte [] msgtype = new byte[1];
        msgtype[0] = 7;
        byte [] msgindex = new byte[4];
        int filesize = config.FileSize;
        int piecesize = config.PieceSize;


        byte [] piecedata = new byte [piecesize];  // filedata

        int length = 1 + index.length + piecedata.length;  //message length

        byte [] piece = ConstantMethod.mergeBytes(ConstantMethod.intToBytes(length)
                ,ConstantMethod.mergeBytes(msgtype,ConstantMethod.mergeBytes(index, piecedata)));

        // whole message

        int offset = ConstantMethod.bytesToInt(index) * piecesize ;

        file.seek(offset);
        for(int i = 0;i < piecesize;i++){
            file.write(piece, offset , piecesize);
        }

        //TODO
        return null;
    }

    /**
     * read the pay load of the Actual message
     * store data into a piece class
     * or directly write into the corresponding position of the temp file
     * @param msg
     * @throws IOException
     */
    public static void readPieceMsg(ActualMsg msg) throws IOException{

    }
}