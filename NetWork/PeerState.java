package NetWork;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by zhupd on 2/19/2017.
 */
public class PeerState {
    //<ID, statelist>
    HashMap<Integer, onePeerState> stateMap = new HashMap<>();
    ArrayList<Integer> preferedNeighbors;
    int optNeighbor;
    ArrayList<Integer> previousNeighbor;


    /**
     * unchoke interval
     */
    void reSelectPreferNeighbors() {
        //select k by uploadSpeed;
        //if file completely download, randomly chose k

        ArrayList<Integer> UnchokeList=null;
        ArrayList<Integer> ChokeList=null;

        //

        sendChoke(ChokeList);
        sendUnchoke(UnchokeList);
    }

    void sendUnchoke(ArrayList<Integer> list) {

    }

    void sendChoke(ArrayList<Integer> list) {

    }


    /**
     * opt unchoke internal
     */
    void reSelectOptNeighbor() {
        //interested && choke
        //randomly chose one
        ArrayList<Integer> Optlist=null;
        int previousOpt=optNeighbor;
        optNeighbor=0;//todo



        sendChoke(new ArrayList<>(previousOpt));
        sendUnchoke(new ArrayList<>(optNeighbor));

    }






}

class onePeerState {
    int ID;
    Socket socket;
    int uploadSpeed;
    private BitField bitField;
    private boolean isInterested;
    private boolean isChoke;

    void updateBitField(ActualMsg msg) {
        //msg: piece/type
        //according msg type to update bitfield.
    }

    /**
     * return if we interested in this peer's piece
     * @param index
     * @return
     */
    boolean isInterested(int index){
        return bitField.isInterested(index);
    }

    /**
     * compare whole bitfield to determine if interested in
     * any file piece
     * @param bitField
     * @return
     */
    boolean compareBitfield(BitField bitField){
        return this.bitField.compareBitField(bitField);
    }

    /**
     * Simply return bitfield
     * @return
     */
    BitField getBitField(){
        return bitField;
    }

    void setInterested(boolean interested){
        isInterested = interested;
    }

    void setChoke(boolean choke){
        isChoke = choke;
    }

    /**
     * add index into the interest list
     * @param index
     */
    void addInterest(int index){
        bitField.addInterest(index);
    }

    /**
     * remove index from the interest list
     * @param index
     */
    void removeInterest(int index){
        bitField.removeInterest(index);
    }

    /**
     * pick an interested index randomly
     * @return
     */
    int randomSelectIndex(){
        Random r = new Random(ID);
        return bitField.randomSelectIndex(r);
    }

}
