package NetWork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zhupd on 4/7/2017.
 */
public class OptNeighbor implements Runnable {
    final int optInterval;
    ArrayList<Config.Peer> allPeer;
    ArrayList<Config.Peer> curNeighbors;
    ArrayList<Config.Peer> tobeChose;
    Config.Peer lastOpt;
    Config.Peer curOpt;

    OptNeighbor() {
        optInterval = peerProcess.config.getOptUnchokingInterval();
        allPeer = peerProcess.config.getPeers();
        curNeighbors = peerProcess.getNeighbourPeers();
        lastOpt = null;
        curOpt = null;
    }

    @Override
    public void run() {
        boolean t = true;
        try {
            while (t) {
                choseOpe();
                Thread.sleep(optInterval * 1000);

                if (peerProcess.config.getMyFile()) {
                    t = false;
                    for (Config.Peer peer : allPeer
                            ) {
                        if (!peer.getHaveFile()) {
                            t = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void choseOpe() throws IOException {
        for (Config.Peer x : allPeer) {
            if (x.getChoked() && x.getInterestMe()) {
                tobeChose.add(x);
            }
        }

        curOpt = tobeChose.get((int) (Math.random() * tobeChose.size()));
        curOpt.setChoked(false);
        curOpt.setOptimisticNeighbor(true);
        ActualMsg msg = new ActualMsg(ActualMsg.MsgType.UNCHOKE);
        msg.sendActualMsg(curOpt.getSocket().getOutputStream());


        if (lastOpt != null) {
            if (!lastOpt.getPreferedNeighbor()) {
                lastOpt.setChoked(true);
                lastOpt.setOptimisticNeighbor(false);
                ActualMsg ckMst = new ActualMsg(ActualMsg.MsgType.CHOKE);
                ckMst.sendActualMsg(lastOpt.getSocket().getOutputStream());
            }
            lastOpt.setTransRate(lastOpt.getTransNumber() / optInterval);
            lastOpt.setTransNumber(0);
        }

        lastOpt = curOpt;

    }

}