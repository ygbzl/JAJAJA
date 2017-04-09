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
    Boolean haveFile;

    BitField(byte[] payload) {
        //this constructor will never be called
        data=payload;
    }

    BitField(Boolean haveFile, int pieceNum) {
        //this constructor is for peers.
        this.haveFile = haveFile;
        data = new byte[pieceNum];
        bdata = new Boolean[pieceNum];
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
            }
        }
    }

    BitField(Boolean haveFile, int pieceNum, int isMe) {
        //this constructor is for myself, the value of isMe doesn't matter.
        this.haveFile = haveFile;
        data = new byte[pieceNum];
        bdata = new Boolean[pieceNum];
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
        for (int i : interestList
             ) {
            if (guestBitfield.getBdata()[i]){
                return true;
            }
        }
        return false;
    }

    boolean isInterested(int index){
        //check if interested in the index piece
        //
        return interestList.contains(index);
    }

    boolean isInterested(){
        return !interestList.isEmpty();
    }

    /*void addInterest(int index){
        interestList.add(index);

    }*/

    synchronized void removeInterest(int index){
        if (interestList.contains(index)) {
            interestList.remove(interestList.indexOf(index));
        }
    }

    synchronized void setInterest(int intex){
        if(!isInterested(intex)) {
            interestList.add(intex);
        }
    }

    synchronized int randomSelectIndex(Random r){

        return interestList.get(r.nextInt(interestList.size()));

    }

    synchronized void setPiece(int index) {
        if (index < data.length) {
            data[index] = 1;
            bdata[index] = true;
        }

        for (Boolean aBdata : bdata) {
            if (!aBdata) {
                return;
            }
        }
        haveFile = true;
    }

    synchronized void removePiece(int index){
        if (index < data.length) {
            data[index] = 0;
            bdata[index] = false;
        }
    }

    Boolean getHaveFile() {
        return haveFile;
    }

    byte[] getData() {
        return data;
    }

    Boolean[] getBdata() {
        return bdata;
    }

    int getLength(){
        return data.length;
    }
}
