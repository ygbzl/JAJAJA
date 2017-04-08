package NetWork;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by challengezwb on 4/7/17.
 */
public class PickPreferNeighbour implements Runnable{
     private int interval;
     private Config config;
     private ArrayList<Config.Peer> peers;
     private BitField bitField;
     private boolean unchoke;
     private ActualMsg actualMsg;
     private int pid;
     private int number = config.getNumberOfPreferedNeighbors();


     public PickPreferNeighbour() {
         this.interval = config.getUnchokinInterval();
         this.peers = config.getPeers();
         this.pid = config.getMyPid();
     }

    @Override
    public void run() {
         firstChoose();
         while(true){
           ArrayList<Config.Peer> temp = peers;
           peers = choose(temp);
         }
    }

    private ArrayList<Config.Peer> choose(ArrayList<Config.Peer> neighbourPeers) {
        return null;
    }

    public void firstChoose(){
        //Random random = new Random(config.getNumberOfPreferedNeighbors());
        for(int i = 0;i < number;i++){
            Random random = new Random(number);
            int index = peerProcess.neighbourPeers.indexOf(random);
            peerProcess.getNeighbourPeers().get(index).choked = false;
            peerProcess.getNeighbourPeers().get(index).preferedNeighbor = true;
            peers.add(peerProcess.neighbourPeers.get(index));

        }
    }

    public Config.Peer[] sortPeers(){
           return null;
    }



}
