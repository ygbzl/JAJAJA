package NetWork;

import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Created by zhupd on 4/10/2017.
 */
public class Logger {
    Config config;
    File file;
    int peerID;
    BufferedWriter out;
    String hostPeerID;
    boolean flag=true;//system out
    
    Logger(Config config) throws FileNotFoundException {
        this.config=config;
        peerID=config.getMyPid();
        hostPeerID= config.getMyPid()+"";
        file = new File("log_peer_" + peerID + ".log");
        try {
            file.createNewFile();
            out = new BufferedWriter(new FileWriter(file));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized void print(String log) throws IOException {
        if (flag) {
            System.out.println(log);

        } else {
            out.write(log);
            out.flush();
        }
        
    }
    
    public void connection(int remotePeerID) throws IOException {
        String time= LocalTime.now().toString();
        String remoteID = remotePeerID + "";
        String log = time + ": Peer " + hostPeerID + " is connected " +
                "from Peer " + remoteID + ".";
       print(log);
    }

    public void changePrefer(ArrayList<Config.Peer> list) throws IOException {
        String ids="";
        for (Config.Peer x : list) {
            ids += x.getPID() + ", ";
        }
        ids = ids.substring(0, ids.length() - 1);
        String time= LocalTime.now().toString();
        String log = time + ": Peer" + hostPeerID + " has the preferred neighbors "
                + ids+ ".";
        print(log);

    }

    public void changeOpt(int id) throws IOException {
        String time= LocalTime.now().toString();
        String remoteID = id + "";
        String log = time + ": Peer" + hostPeerID + " has the optimistically unchoked neighbor "
                + remoteID+ ".";
        print(log);
    }

    public void unchoking(int id) throws IOException {
        String time= LocalTime.now().toString();
        String remoteID = id + "";
        String log = time + ": Peer" + hostPeerID + " is unchoked by "
                + remoteID+ ".";
        print(log);

    }

    public void choking(int id) throws IOException {
        String time= LocalTime.now().toString();
        String remoteID = id + "";
        String log = time + ": Peer" + hostPeerID + " is choked by "
                + remoteID+ ".";
        print(log);
    }

    public void have(int id, int pieceIndex) throws IOException {
        String time= LocalTime.now().toString();
        String remoteID = id + "";
        String log = time + ": Peer" + hostPeerID + " received the ‘have’ message from "
                + remoteID + " for the piece " + pieceIndex + ".";
        print(log);
    }

    public void interested(int id) throws IOException {
        String time= LocalTime.now().toString();
        String remoteID = id + "";
        String log = time + ": Peer" + hostPeerID + " received the ‘interested’ message from "
                + remoteID+ ".";
        print(log);
    }

    public void notInterested(int id) throws IOException {
        String time= LocalTime.now().toString();
        String remoteID = id + "";
        String log = time + ": Peer" + hostPeerID + " received the ‘not interested’ message from "
                + remoteID+ ".";
        print(log);
    }

    public void piece(int id, int number) throws IOException {
        String time= LocalTime.now().toString();
        String remoteID = id + "";
        String log = time + ": Peer" + hostPeerID + " has downloaded the piece "
                + number + " from " + remoteID + ".";
        print(log);
    }

    public void completion() throws IOException {
        String time= LocalTime.now().toString();
        String log = time + ": Peer" + hostPeerID + "has downloaded the complete file.";
        print(log);
    }
}
