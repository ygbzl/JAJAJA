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

        file = new RandomAccessFile(directory + config.getFileName(), "rw");


    }

    public synchronized  FilePiece readMsg(int index) throws IOException {
        int length = 0;
        if(index == config.getPieceNum()-1){
            length = config.getRemainPieceSize();
        } else {
            length = config.getPieceSize();
        }
        int offset = index * config.getPieceSize();
        byte[] data = new byte[length];

        try {
            file.seek(offset);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < length; i++) {
            data[i] = file.readByte();
        }

        FilePiece filePiece = new FilePiece(index, data);
        return filePiece;
    }

    

    public synchronized void writeMsg(FilePiece filePiece) throws IOException {
        int offset = filePiece.getPieceIndex()*config.getPieceSize();
        byte[] data = filePiece.getPiecesArray();
        file.seek(offset);
        file.write(data, offset, data.length);
    }

}