package NetWork;

import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Created by zhupd on 4/10/2017.
 */
public class Logger {
    private Config config;
    private File file;
    private int peerID;
    private BufferedWriter out;
    private String hostPeerID;
    private boolean flag=false;//system out
    private FileWriter temp;
    String text;

    Logger(Config config) {
        text = "";
        this.config=config;
        peerID=config.getMyPid();
        hostPeerID= config.getMyPid()+"";
        file = new File("peer_" + config.getMyPid() + "/" + "log_peer_" + peerID + ".log");
        try {
            file.createNewFile();
            temp=new FileWriter(file);

            out = new BufferedWriter(temp);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*public synchronized void print(String log) throws IOException {
        log += "\n";
        if (flag) {
            System.out.println(log);

        } else {
            out.write(log);
            out.flush();
        }

    }*/

    public synchronized void print(String log) throws IOException {

    }

    public synchronized void jaa() throws IOException {
        if (flag) {
            System.out.println(text);

        } else {
            out.write(text);
            out.flush();
        }
    }

    public void connection(int remotePeerID) throws IOException {
        String time= LocalTime.now().toString();
        String remoteID = remotePeerID + "";
        String log = time + ": Peer " + hostPeerID + " is connected " +
                "from Peer " + remoteID + ".";
        text += log + "\n";
        print(log);
    }

    public void changePrefer(ArrayList<Config.Peer> list) throws IOException {
        String ids="";
        for (Config.Peer x : list) {
            ids += x.getPID() + ", ";
        }
        if (ids.length() > 0) {
            ids = ids.substring(0, ids.length() - 2);
            String time = LocalTime.now().toString();
            String log = time + ": Peer " + hostPeerID + " has the preferred neighbors "
                    + ids + ".";
            text += log + "\n";
            print(log);
        }

    }

    public void changeOpt(int id) throws IOException {
        String time= LocalTime.now().toString();
        String remoteID = id + "";
        String log = time + ": Peer " + hostPeerID + " has the optimistically unchoked neighbor "
                + remoteID+ ".";
        text += log + "\n";
        print(log);
    }

    public void unchoking(int id) throws IOException {
        String time= LocalTime.now().toString();
        String remoteID = id + "";
        String log = time + ": Peer " + hostPeerID + " is unchoked by "
                + remoteID+ ".";
        text += log + "\n";

        print(log);

    }

    public void choking(int id) throws IOException {
        String time= LocalTime.now().toString();
        String remoteID = id + "";
        String log = time + ": Peer " + hostPeerID + " is choked by "
                + remoteID+ ".";
        text += log + "\n";

        print(log);
    }

    public void have(int id, int pieceIndex) throws IOException {
        String time= LocalTime.now().toString();
        String remoteID = id + "";
        String log = time + ": Peer " + hostPeerID + " received the ‘have’ message from "
                + remoteID + " for the piece " + pieceIndex + ".";
        text += log + "\n";

        print(log);
    }

    public void interested(int id) throws IOException {
        String time= LocalTime.now().toString();
        String remoteID = id + "";
        String log = time + ": Peer " + hostPeerID + " received the ‘interested’ message from "
                + remoteID+ ".";
        text += log + "\n";

        print(log);
    }

    public void notInterested(int id) throws IOException {
        String time= LocalTime.now().toString();
        String remoteID = id + "";
        String log = time + ": Peer " + hostPeerID + " received the ‘not interested’ message from "
                + remoteID+ ".";
        text += log + "\n";

        print(log);
    }

    public void piece(int id, int number, int index) throws IOException {
        String time= LocalTime.now().toString();
        String remoteID = id + "";
        String log = time + ": Peer " + hostPeerID + " has downloaded the piece "
                + index + " from " + remoteID + "." + " Now the number of pieces it has is " + number +".";
        text += log + "\n";

        print(log);
    }

    public void completion() throws IOException {
        String time= LocalTime.now().toString();
        String log = time + ": Peer " + hostPeerID + " has downloaded the complete file.";
        text += log + "\n";

        print(log);
        //jaa(text);

    }

    public void loggerOf() throws Exception{
        jaa();
        out.flush();
        out.close();
        temp.close();

    }
}
