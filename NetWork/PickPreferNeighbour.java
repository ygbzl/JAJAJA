package NetWork;

import java.util.ArrayList;

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


     public PickPreferNeighbour(int interval, Config config, ArrayList<Config.Peer> peers, BitField bitField, int pid) {
         this.interval = interval;
         this.config = config;
         this.peers = peers;
         this.bitField = bitField;
         this.unchoke = unchoke;
         this.pid = pid;
     }

    @Override
    public void run() {
         while(true){

         }



    }
}
