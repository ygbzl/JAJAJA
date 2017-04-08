package NetWork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by challengezwb on 4/7/17.
 */
public class PickPreferNeighbour implements Runnable{
     private int interval;
     private ArrayList<Config.Peer> preferedPeers;
     private ActualMsg actualMsg;
     private int number;


     public PickPreferNeighbour() throws IOException{
         this.interval = peerProcess.config.getUnchokinInterval();
         this.number = peerProcess.config.getNumberOfPreferedNeighbors();
     }

    @Override
    public void run(){
        try {
            firstChoose();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<Config.Peer> choose(ArrayList<Config.Peer> neighbourPeers) {
        return null;
    }

    public void firstChoose() throws IOException{
        //Random random = new Random(config.getNumberOfPreferedNeighbors());
        for(int i = 0;i < number;i++){
            Random random = new Random(number);
            int index = peerProcess.neighbourPeers.indexOf(random);
            Config.Peer temp = peerProcess.getNeighbourPeers().get(index);
            temp.setChoked(false);
            temp.setPreferedNeighbor(true);
            ActualMsg msg = new ActualMsg(ActualMsg.MsgType.UNCHOKE);
            msg.sendActualMsg(temp.getSocket().getOutputStream());
            preferedPeers.add(peerProcess.neighbourPeers.get(index));
        }
    }

    public Config.Peer[] sortPeers(){
           return null;
    }



}
