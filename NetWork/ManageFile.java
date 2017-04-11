package NetWork;

/**
 * Created by zhupd on 2/20/2017.
 */
import java.io.*;
import java.nio.file.Files;

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
     * @throws FileNotFoundException
     */

    public ManageFile(Config config) throws IOException {
        ManageFile.config = config;

        String directory = "peer_" + config.getMyPid() + "/";

        File dir = new File(directory);

        if (!dir.exists()) {
            dir.mkdirs();
        }


        if (config.getMyFile()) {
            File temp = new File(config.getFileName());
            copyfile(temp, dir);
        } else {
            file = new RandomAccessFile(directory + config.getFileName(), "rw");
            try {
                file.setLength(config.getFileSize());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void copyfile(File source, File dest)
            throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }


    public synchronized FilePiece readMsg(int index) throws IOException {
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
        file.write(data);
    }

    public void closeManageFile () throws IOException {
        file.close();
    }
}