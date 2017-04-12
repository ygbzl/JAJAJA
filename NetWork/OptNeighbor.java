package NetWork;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zhupd on 4/7/2017.
 */
public class OptNeighbor implements Runnable {
    private final int optInterval;
    ArrayList<Config.Peer> allPeer;
    //ArrayList<Config.Peer> curNeighbors;
    ArrayList<Config.Peer> tobeChose;
    Config.Peer lastOpt;
    Config.Peer curOpt;
    Random r;
    Logger logger;

    OptNeighbor() throws FileNotFoundException{
        optInterval = peerProcess.config.getOptUnchokingInterval();
        allPeer = peerProcess.config.getPeers();
        //curNeighbors = peerProcess.getNeighbourPeers();
        tobeChose = new ArrayList<>();
        lastOpt = null;
        curOpt = null;
        r = new Random(System.currentTimeMillis());
        logger = peerProcess.logger;
    }

    @Override
    public void run() {
        //boolean t = true;
        try {
            /*while (!peerProcess.ifFirstPreferFinished) {

            }*/
            Thread.sleep(1000);
            //int times = 0;
            while (true) {
                //System.out.println("optimistic neighbor, times: "+times++);
                choseOpe();
                //logger.changeOpt(curOpt.getPID());
                tobeChose.clear();

                /*if (peerProcess.config.getMyBitField().getHaveFile()) {
                    boolean t = false;
                    for (Config.Peer peer : allPeer) {
                        if (!peer.getBitField().getHaveFile()) {
                            t = true;
                        }
                    }

                    if (!t){
                        peerProcess.config.setIscompleted(true);
                        System.out.println("optimistic neighbor thread exit 0.");
                        return;
                    }
                }*/

                Thread.sleep(optInterval * 1000);

            }
        } catch (IOException e) {
            //e.printStackTrace();
            //System.out.println("optimistic neighbor thread exit 1.");
            return;
        } catch (InterruptedException e) {
            //e.printStackTrace();
            //System.out.println("optimistic neighbor thread exit 2.");
            return;
        }
    }

    public synchronized void choseOpe() throws IOException {
        for (Config.Peer x : allPeer) {
            if (x.getChoked() && x.getInterestMe()) {
                tobeChose.add(x);
            }
        }
        if (tobeChose.size() == 0){
            return;
        }
        curOpt = tobeChose.get(r.nextInt(tobeChose.size()));

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
                logger.changeOpt(curOpt.getPID());
            }
            lastOpt.setTransRate(lastOpt.getTransNumber() / optInterval);
            lastOpt.setTransNumber(0);
        }

        lastOpt = curOpt;

    }

}