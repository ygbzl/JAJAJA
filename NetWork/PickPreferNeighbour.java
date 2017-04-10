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
     Random random = new Random(System.currentTimeMillis());
     private Logger logger;


     public PickPreferNeighbour() throws IOException{
         this.interval = peerProcess.config.getUnchokinInterval();
         this.number = peerProcess.config.getNumberOfPreferedNeighbors();
         this.preferedPeers = peerProcess.getNeighbourPeers();
         this.logger = new Logger(peerProcess.config);
     }

    @Override
    public void run(){
        try {
            firstChoose();
            logger.changePrefer(preferedPeers);
            boolean t = true;
            while(t){
                if (peerProcess.config.getMyFile()) {
                    firstChoose();
                }else {
                    choose();
                }
                Thread.sleep(interval * 1000);
                if (peerProcess.config.getMyFile()) {
                    t = false;
                    for (Config.Peer peer : peerProcess.config.getPeers()
                            ) {
                        if (!peer.getHaveFile()) {
                            t = true;
                        }
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void choose() throws IOException{
        preferedPeers = sortPeers();
        for(Config.Peer peer : preferedPeers){
            int speed = peer.getTransNumber() / interval;
            if(!peer.getChoked() && peer.getPreferedNeighbor()){
                peer.setTransRate(speed);
            }else if(peer.getOptimisticNeighbor()){
                peer.setPreferedNeighbor(true);
                peer.setTransRate(speed);
            }else{
                peer.setChoked(false);
                peer.setPreferedNeighbor(true);
                ActualMsg msg = new ActualMsg(ActualMsg.MsgType.UNCHOKE);
                logger.changePrefer(preferedPeers);
                msg.sendActualMsg(peer.getSocket().getOutputStream());
                peer.setTransRate(speed);
            }
        }
        for(Config.Peer peer : peerProcess.config.getPeers()){
            if(!peer.getChoked() && !preferedPeers.contains(peer) && peer.getOptimisticNeighbor()){
                //continue;
            }else if(!peer.getChoked() && !preferedPeers.contains(peer) && !peer.getOptimisticNeighbor()){
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

    public void firstChoose() throws IOException{
        //Random random = new Random(config.getNumberOfPreferedNeighbors());
        if(peerProcess.getNeighbourPeers().size() < number){
            for(int i = 0;i < peerProcess.getNeighbourPeers().size();i++){
                Config.Peer temp = peerProcess.getNeighbourPeers().get(i);
                temp.setChoked(false);
                temp.setPreferedNeighbor(true);
                ActualMsg msg = new ActualMsg(ActualMsg.MsgType.UNCHOKE);
                logger.changePrefer(preferedPeers);
                msg.sendActualMsg(temp.getSocket().getOutputStream());
                preferedPeers.add(peerProcess.getNeighbourPeers().get(i));
            }
        }
        for(int i = 0;i < number;i++){
            int index = peerProcess.getNeighbourPeers().indexOf(peerProcess.getNeighbourPeers().get(random.nextInt(peerProcess.getNeighbourPeers().size())));
            Config.Peer temp = peerProcess.getNeighbourPeers().get(index);
            temp.setChoked(false);
            temp.setPreferedNeighbor(true);
            ActualMsg msg = new ActualMsg(ActualMsg.MsgType.UNCHOKE);
            logger.changePrefer(preferedPeers);
            msg.sendActualMsg(temp.getSocket().getOutputStream());
            preferedPeers.add(peerProcess.getNeighbourPeers().get(index));

        }
    }

    public ArrayList<Config.Peer> sortPeers(){
        PriorityQueue<Config.Peer> pq = new PriorityQueue<>(number+1, Comparator.comparingInt(Config.Peer::getTransRate));
        for (Config.Peer x : peerProcess.config.getPeers()) {
            if(pq.size()==number+1){
                pq.poll();
            }
            pq.offer(x);
        }
        pq.poll();
        return new ArrayList<>(pq);
        //return res;
    }

}
