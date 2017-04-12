package NetWork;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by zhupd on 2/19/2017.
 */
public class BitField {
    private byte[] data;
    private Boolean[] bdata;
    private ArrayList<Integer> interestList; //which pieces i am interested in
    private Boolean haveFile;
    private Lock lock;
    private Lock intlock;

    BitField(byte[] payload) {
        //this constructor will never be called
        data=payload;
    }

    BitField(boolean haveFile, int pieceNum) {
        //this constructor is for peers.
        this.haveFile = haveFile;
        data = new byte[pieceNum];
        bdata = new Boolean[pieceNum];
        interestList = new ArrayList<>();
        lock = new ReentrantLock();
        intlock = new ReentrantLock();
        if (haveFile) {
            for (int i = 0; i < data.length; i++) {
                data[i] = 1;
                bdata[i] = true;
                interestList.add(i);
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
        lock = new ReentrantLock();
        intlock = new ReentrantLock();
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
        intlock.lock();
        try {
            if (interestList.contains(index)) {
                interestList.remove(interestList.indexOf(index));
            }
        } finally {
            intlock.unlock();
        }
    }

    synchronized void setInterest(int index){
        intlock.lock();
        try {
            if(!isInterested(index)) {
                interestList.add(index);
            }
        } finally {
            intlock.unlock();
        }
    }

    synchronized int randomSelectIndex(Random r){
        if (interestList.size() == 0){
            try {
                throw new Exception("nothing in interest list");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //return -1;
        }
        return interestList.get(r.nextInt(interestList.size()));

    }

    synchronized void setPiece(int index) {
        lock.lock();
        try {
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
        } finally {
            lock.unlock();
        }
    }

    synchronized void removePiece(int index){
        if (index < data.length) {
            data[index] = 0;
            bdata[index] = false;
        }
    }

    Boolean getHaveFile() {
        lock.lock();
        boolean t;
        try {
            t = haveFile;
        } finally {
            lock.unlock();
        }

        return t;
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

    void setHaveFile(Boolean haveFile) {
        this.haveFile = haveFile;
    }
}
