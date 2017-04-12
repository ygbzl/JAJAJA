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
    private Random random = new Random(System.currentTimeMillis());
    private Logger logger;


    public PickPreferNeighbour() {
        this.interval = peerProcess.config.getUnchokinInterval();
        this.number = peerProcess.config.getNumberOfPreferedNeighbors();
        this.preferedPeers = new ArrayList<>();
        this.logger = peerProcess.logger;
    }

    @Override
    public void run() {
        try {
            //int times = 0;
            //System.out.println("prefered neighbor thread, times: "+times++);
            firstChoose();
            //peerProcess.ifFirstPreferFinished = true;
            /*if(!firstChoose()) {
                System.out.println("prefer neighbor thread exit 0.");
                return;
            }*/
            //logger.changePrefer(preferedPeers);
            //boolean t = true;
            Thread.sleep(interval * 1000);
            while (true) {
                //System.out.println("prefered neighbor thread, times: "+times++);
                if (peerProcess.config.getMyFile()) {
                    firstChoose();
                } else {
                    choose();
                }

                /*if (peerProcess.config.getMyBitField().getHaveFile()) {
                    boolean t = false;
                    for (Config.Peer peer : peerProcess.config.getPeers()) {
                        if (!peer.getBitField().getHaveFile()) {
                            t = true;
                        }
                    }

                    if (!t) {
                        peerProcess.config.setIscompleted(true);
                        System.out.println("prefer neighbor thread exit 1.");
                        return;
                    }
                }*/
                Thread.sleep(interval * 1000);
            }
        } catch (IOException | InterruptedException e) {
            //e.printStackTrace();
            //System.out.println("prefer neighbor thread exit 2.");
            return;
        }

    }

    public void choose() throws IOException {
        //preferedPeers = sortPeers();
        boolean ifchange = false;

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
                ifchange = true;
            } else {
                peerProcess.config.getPeers().get(peerProcess.config.getPeers().indexOf(peer)).setPreferedNeighbor(true);
                ActualMsg unchoke = new ActualMsg(ActualMsg.MsgType.UNCHOKE);
                peerProcess.config.getPeers().get(peerProcess.config.getPeers().indexOf(peer)).setChoked(false);
                unchoke.sendActualMsg(peer.getSocket().getOutputStream());
                ifchange = true;
            }
        }

        for (Config.Peer peer:preferedPeers) {
            if (!tempPfs.contains(peer) && !peer.getOptimisticNeighbor()){
                peerProcess.config.getPeers().get(peerProcess.config.getPeers().indexOf(peer)).setPreferedNeighbor(false);
                peerProcess.config.getPeers().get(peerProcess.config.getPeers().indexOf(peer)).setChoked(true);
                ActualMsg choke = new ActualMsg(ActualMsg.MsgType.CHOKE);
                choke.sendActualMsg(peer.getSocket().getOutputStream());
                ifchange = true;
            }
        }

        preferedPeers.clear();
        preferedPeers = tempPfs;
        if (ifchange) {
            logger.changePrefer(preferedPeers);
        }

    }

    public void firstChoose() throws IOException {
        //Random random = new Random(config.getNumberOfPreferedNeighbors());
        preferedPeers.clear();
        if (peerProcess.config.getPeers().size() <= number) {
            for (Config.Peer peer:peerProcess.config.getPeers()) {
                if (peer.getInterestMe()) {
                    peer.setChoked(false);
                    peer.setPreferedNeighbor(true);
                    ActualMsg unchoke = new ActualMsg(ActualMsg.MsgType.UNCHOKE);
                    unchoke.sendActualMsg(peer.getSocket().getOutputStream());
                    preferedPeers.add(peer);
                }
            }

            logger.changePrefer(preferedPeers);

            //return false;
        } else {
            for (int i = 0; i < number; i++) {
                int index = peerProcess.config.getPeers().indexOf(peerProcess.config.getPeers().get(random.nextInt(peerProcess.config.getPeers().size())));
                Config.Peer peer = peerProcess.config.getPeers().get(index);
                if (!preferedPeers.contains(peer) && peer.getInterestMe()) {
                    preferedPeers.add(peer);
                    peer.setChoked(false);
                    peer.setPreferedNeighbor(true);
                    ActualMsg unchoke = new ActualMsg(ActualMsg.MsgType.UNCHOKE);
                    unchoke.sendActualMsg(peer.getSocket().getOutputStream());
                } else {
                    i--;
                }
            }
            logger.changePrefer(preferedPeers);
            //return true;
        }
    }

    public ArrayList<Config.Peer> sortPeers() {
        PriorityQueue<Config.Peer> pq = new PriorityQueue<>(number + 1, Comparator.comparingInt(Config.Peer::getTransRate));
        for (Config.Peer x : peerProcess.config.getPeers()) {
            if (x.getInterestMe()) {
                if (pq.size() == number + 1) {
                    pq.poll();
                }
                pq.offer(x);
            }
        }
        pq.poll();
        return new ArrayList<>(pq);
        //return res;
    }

}
