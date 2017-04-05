package NetWork;

import java.util.ArrayList;
import java.util.Random;


/**
 * Created by zhupd on 2/19/2017.
 */
public class BitField {
    byte[] data;
    private ArrayList<Integer> interestList;

    BitField(byte[] payload) {
        data=payload;
    }

    boolean compareBitField(BitField guestBitfield) {
        //return whether is interested.
        //also update the interest List.
        return false;
    }

    boolean isInterested(int index){
        //check if interested in the index piece
        //
        return false;
    }

    void addInterest(int index){

        interestList.add(index);

    }

    void removeInterest(int index){

        interestList.remove(interestList.indexOf(index));

    }

    int randomSelectIndex(Random r){

        return r.nextInt(interestList.size());

    }

}
