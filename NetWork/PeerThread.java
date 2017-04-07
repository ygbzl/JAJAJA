package NetWork;


/**
 * Created by leqi on 2017/4/7.
 */
public class PeerThread implements Runnable {
    ActualMsg messager;
    Config config;
    ManageFile fileManager;
    Config.Peer peer;

    PeerThread(Config config, ManageFile fileManager, Config.Peer peer){
        messager = new ActualMsg();
        this.config = config;
        this.fileManager = fileManager;
        this.peer = peer;
    }

    @Override
    public void run() {

    }
}
