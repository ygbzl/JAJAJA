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

    public ManageFile(Config config, int myID) throws FileNotFoundException {
        this.config = config;

        String directory = "peer_" + myID + "/";

        File dir = new File(directory);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        file = new RandomAccessFile(directory + config.FileName, "rw");

    }

    public synchronized MakePieces readPieceMsg(int pieceIndex) throws IOException {
        int messageLength = 0;
        if (pieceIndex == config.PieceSize - 1) {
            messageLength = config.FileSize - config.PieceSize * pieceIndex;
        } else {
            messageLength = config.PieceSize;
        }

        int fileStart = pieceIndex * config.getPieceSize();
        file.seek(fileStart);
        Byte[] fileByte = new Byte[messageLength];
        for (int i = 0; i < messageLength; i++) {
            fileByte[i] = file.readByte();
        }
        MakePieces makePieces;
        makePieces = new MakePieces(pieceIndex, fileByte);
        return makePieces;
    }

    /**
     * fetch file piece according to the index
     * packed into a piece message and transform into a byte stream
     * and return
     *
     * @paramindex
     * @return byte[]
     * @throws IOException
     */

    public synchronized void writePieceMsg(MakePieces makePieces) throws IOException {
        int startPosition = makePieces.getPieceIndex() * config.getPieceSize();
        int pieceslength = makePieces.getPiecesArray().length;
        Byte[] piecesByte = makePieces.getPiecesArray();
        file.seek(startPosition);
        for (int i = 0; i < pieceslength; i++) {
            file.write(piecesByte[i]);
        }
    }


    /**
     * read the pay load of the Actual message
     * store data into a piece class
     * or directly write into the corresponding position of the temp file
     * @param msg
     * @throws IOException
     */
}