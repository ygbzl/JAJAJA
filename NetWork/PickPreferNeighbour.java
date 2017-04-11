package NetWork;

import java.io.IOException;
import java.util.*;

/**
 * Created by challengezwb on 4/7/17.
 */
public class PickPreferNeighbour implements Runnable {
    private int interval;
    private ArrayList<Config.Peer> preferedPeers;
    private int number;
    Random random = new Random(System.currentTimeMillis());
    private Logger logger;


    public PickPreferNeighbour() throws IOException {
        this.interval = peerProcess.config.getUnchokinInterval();
        this.number = peerProcess.config.getNumberOfPreferedNeighbors();
        this.preferedPeers = new ArrayList<>();
        this.logger = new Logger(peerProcess.config);
    }

    @Override
    public void run() {
        try {
            if(!firstChoose())
                return;
            logger.changePrefer(preferedPeers);
            //boolean t = true;
            int times = 0;
            while (true) {
                System.out.println("prefered neighbor thread, times: "+times++);
                if (peerProcess.config.getMyFile()) {
                    firstChoose();
                } else {
                    choose();
                }
                Thread.sleep(interval * 1000);
                if (peerProcess.config.getMyBitField().getHaveFile()) {
                    boolean t = false;
                    for (Config.Peer peer : peerProcess.config.getPeers()) {
                        if (!peer.getBitField().getHaveFile()) {
                            t = true;
                        }
                    }

                    if (!t) {
                        peerProcess.config.setIscompleted(true);
                        return;
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return;
        }

    }

    public void choose() throws IOException {
        //preferedPeers = sortPeers();
        for (Config.Peer pfPeer : preferedPeers) {
            Config.Peer peer = peerProcess.config.getPeers().get(peerProcess.config.getPeers().indexOf(pfPeer));
            peer.setTransRate(peer.getTransNumber() / interval);
            peer.setTransNumber(0);
        }

        ArrayList<Config.Peer> tempPfs = sortPeers();
        for (Config.Peer peer: tempPfs) {
            if (preferedPeers.contains(peer)) {

            } else if (peer.getOptimisticNeighbor()) {
                peerProcess.config.getPeers().get(peerProcess.config.getPeers().indexOf(peer)).setPreferedNeighbor(true);
            } else {
                peerProcess.config.getPeers().get(peerProcess.config.getPeers().indexOf(peer)).setPreferedNeighbor(true);
                ActualMsg unchoke = new ActualMsg(ActualMsg.MsgType.UNCHOKE);
                peerProcess.config.getPeers().get(peerProcess.config.getPeers().indexOf(peer)).setChoked(false);
                unchoke.sendActualMsg(peer.getSocket().getOutputStream());
            }
        }

        for (Config.Peer peer:preferedPeers) {
            if (!tempPfs.contains(peer) && !peer.getOptimisticNeighbor()){
                peerProcess.config.getPeers().get(peerProcess.config.getPeers().indexOf(peer)).setPreferedNeighbor(false);
                peerProcess.config.getPeers().get(peerProcess.config.getPeers().indexOf(peer)).setChoked(true);
                ActualMsg choke = new ActualMsg(ActualMsg.MsgType.CHOKE);
                choke.sendActualMsg(peer.getSocket().getOutputStream());
            }
        }

        preferedPeers.clear();
        preferedPeers = tempPfs;

    }

    public boolean firstChoose() throws IOException {
        //Random random = new Random(config.getNumberOfPreferedNeighbors());
        if (peerProcess.config.getPeers().size() < number) {
            for (Config.Peer peer:peerProcess.config.getPeers()) {
                peer.setChoked(false);
                peer.setPreferedNeighbor(true);
                ActualMsg unchoke = new ActualMsg(ActualMsg.MsgType.UNCHOKE);
                unchoke.sendActualMsg(peer.getSocket().getOutputStream());
                preferedPeers.add(peer);
                logger.changePrefer(preferedPeers);
            }
            return false;
        } else {
            for (int i = 0; i < number; i++) {
                int index = peerProcess.config.getPeers().indexOf(peerProcess.config.getPeers().get(random.nextInt(peerProcess.config.getPeers().size())));
                Config.Peer peer = peerProcess.config.getPeers().get(index);
                if (!preferedPeers.contains(peer)) {
                    preferedPeers.add(peer);
                    peer.setChoked(false);
                    peer.setPreferedNeighbor(true);
                    ActualMsg unchoke = new ActualMsg(ActualMsg.MsgType.UNCHOKE);
                    logger.changePrefer(preferedPeers);
                    unchoke.sendActualMsg(peer.getSocket().getOutputStream());
                } else {
                    i--;
                }
            }
            return true;
        }
    }

    public ArrayList<Config.Peer> sortPeers() {
        PriorityQueue<Config.Peer> pq = new PriorityQueue<>(number + 1, Comparator.comparingInt(Config.Peer::getTransRate));
        for (Config.Peer x : peerProcess.config.getPeers()) {
            if (pq.size() == number + 1) {
                pq.poll();
            }
            pq.offer(x);
        }
        pq.poll();
        return new ArrayList<>(pq);
        //return res;
    }

}
