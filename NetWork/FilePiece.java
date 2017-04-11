package NetWork;

/**
 * Created by challengezwb on 4/5/17.
 */
public class FilePiece {
    /*
   Make file into pieces and store them into piecesArray.
   You can get pieceIndex and piecesArray by these methords below;*/
    private final int pieceIndex;
    private final byte[] piecesArray;

    public FilePiece(ActualMsg actualMsg) {
        byte[] indexTemp = new byte[4];
        for (int i = 0; i < 4; i++) {
            indexTemp[i] = actualMsg.msgPaylod[i];
        }
        pieceIndex = ConstantMethod.bytesToInt(indexTemp);
        piecesArray = new byte[actualMsg.msgPaylod.length - 4];
        for (int i = 4; i < actualMsg.msgPaylod.length; i++) {
            piecesArray[i - 4] = actualMsg.msgPaylod[i];
        }
    }

    public FilePiece(int pieceIndex, byte[] piecesArray) {
        this.pieceIndex = pieceIndex;
        this.piecesArray = piecesArray;
    }

    public int getPieceIndex() {

        return pieceIndex;
    }

    public byte[] getPiecesArray() {

        return piecesArray;
    }

}
