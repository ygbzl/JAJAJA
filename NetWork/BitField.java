package NetWork;

import java.util.ArrayList;
import java.util.Random;


/**
 * Created by zhupd on 2/19/2017.
 */
public class BitField {
    byte[] data;
    Boolean[] bdata;
    private ArrayList<Integer> interestList; //which pieces i am interested in

    BitField(byte[] payload) {
        //this constructor will never be called
        data=payload;
    }

    BitField(Boolean haveFile, int pieceNum) {
        //this constructor is for peers.
        data = new byte[pieceNum];
        if (haveFile) {
            for (int i = 0; i < data.length; i++) {
                data[i] = 1;
                bdata[i] = true;
            }
        } else {
            for (int i = 0; i < data.length; i++) {
                data[i] = 0;
                bdata[i] = false;
            }
        }
    }

    BitField(Boolean haveFile, int pieceNum, int isMe) {
        //this constructor is for myself, the value of isMe doesn't matter.
        data = new byte[pieceNum];
        interestList = new ArrayList<>();
        if (haveFile) {
            for (int i = 0; i < data.length; i++) {
                data[i] = 1;
                bdata[i] = true;
            }
        } else {
            for (int i = 0; i < data.length; i++) {
                data[i] = 0;
                bdata[i] = false;
                interestList.add(i);
            }
        }
    }

    boolean compareBitField(BitField guestBitfield) {
        //return whether is interested.
        //also update the interest List.
        return false;
    }

    boolean isInterested(int index){
        //check if interested in the index piece
        //
        return interestList.contains(index);
    }

    /*void addInterest(int index){
        interestList.add(index);

    }*/

    void removeInterest(int index){
        interestList.remove(interestList.indexOf(index));

    }

    int randomSelectIndex(Random r){

        return r.nextInt(interestList.size());

    }

}
