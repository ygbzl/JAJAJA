package NetWork;

import java.util.List;

/**
 * Created by zhupd on 4/7/2017.
 */
public class OptNeighbor implements Runnable {
    final int optInterval;


    OptNeighbor() {
        optInterval = peerProcess.config.getOptUnchokingInterval();
    }

    @Override
    public void run() {
        try {
            while (true) {
                choseOpe();
                Thread.sleep(optInterval * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void choseOpe() {
        List<Config.Peer>
    }

}