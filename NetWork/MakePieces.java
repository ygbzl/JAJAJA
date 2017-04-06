package NetWork;

/**
 * Created by challengezwb on 4/5/17.
 */
public class MakePieces {
    /*
   Make file into pieces and store them into piecesArray.
   You can get pieceIndex and piecesArray by these methords below;
 */
    private final int pieceIndex;
    private final Byte[] piecesArray;


    public MakePieces(int pieceIndex, Byte[] piecesArray){
        this.pieceIndex = pieceIndex;
        this.piecesArray = piecesArray;
    }

    public int getPieceIndex(){
        return pieceIndex;
    }

    public Byte[] getPiecesArray(){
        return piecesArray;
    }
}
