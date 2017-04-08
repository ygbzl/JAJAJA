package NetWork;

import java.io.IOException;
import java.util.*;

/**
 * Created by challengezwb on 4/7/17.
 */
public class PickPreferNeighbour implements Runnable{
     private int interval;
     private ArrayList<Config.Peer> preferedPeers;
     private int number;


     public PickPreferNeighbour() throws IOException{
         this.interval = peerProcess.config.getUnchokinInterval();
         this.number = peerProcess.config.getNumberOfPreferedNeighbors();
         this.preferedPeers = peerProcess.getNeighbourPeers();


    @Override
    public void run(){
        try {
            firstChoose();
            while(true){
                choose();
                Thread.sleep(interval * 1000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public synchronized void choose() throws IOException{
        preferedPeers = sortPeers();
        for(Config.Peer peer : preferedPeers){
            int speed = peer.getTransNumber() / interval;
            if(peer.getChoked() == false && peer.getPreferedNeighbor() == true){
                peer.setTransRate(speed);
            }else if(peer.getOptimisticNeighbor() == true){
                peer.setPreferedNeighbor(true);
                peer.setTransRate(speed);
            }else{
                peer.setChoked(false);
                peer.setPreferedNeighbor(true);
                ActualMsg msg = new ActualMsg(ActualMsg.MsgType.UNCHOKE);
                msg.sendActualMsg(peer.getSocket().getOutputStream());
                peer.setTransRate(speed);
            }
        }
        for(Config.Peer peer : peerProcess.config.getPeers()){
            if(peer.getChoked() == false && !preferedPeers.contains(peer) && peer.getOptimisticNeighbor() == true){
                continue;
            }else if(peer.getChoked() == false && !preferedPeers.contains(peer) && peer.getOptimisticNeighbor() == false){
                peer.setChoked(true);
                peer.setPreferedNeighbor(false);
                ActualMsg msg = new ActualMsg(ActualMsg.MsgType.CHOKE);
                msg.sendActualMsg(peer.getSocket().getOutputStream());
                peer.setTransNumber(0);
            }else{
                peer.setTransNumber(0);
            }
        }
    }

    public synchronized void firstChoose() throws IOException{
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

    public ArrayList<Config.Peer> sortPeers(){
        PriorityQueue<Config.Peer> pq = new PriorityQueue<>(number+1, (a,b) -> a.getTransRate()-b.getTransRate());
        for (Config.Peer x : peerProcess.config.getPeers()) {
            if(pq.size()==number+1){
                pq.poll();
            }
            pq.offer(x);
        }
        pq.poll();
        ArrayList<Config.Peer> res = new ArrayList<>(pq);
        return res;
    }

}
