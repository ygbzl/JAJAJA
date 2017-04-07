package NetWork;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zhupd on 2/19/2017.
 */
public class Config {
    int NumberOfPreferedNeighbors;
    int UnchokinInterval;
    int OptUnchokingInterval;
    String FileName;
    int FileSize;
    int PieceSize;
    //HashMap<Integer, Peer> peers;
    ArrayList<Peer> peers;
    int pieceNum;
    int remainPieceSize;

    int myPid;
    String myAddr;
    int myPort;
    Boolean myFile;
    BitField myBitField;
    int myIndex;

    Config(int pid) throws IOException {
        myPid = pid;
        readCommon();
        readPeerInfo(myPid);
    }

    /**
     * read common.cfg
     * set:
     * max number of preferred neighbor
     * reselect preferred neighbor interval
     * reselect optimistically neighbor interval
     * <p>
     * fetch:
     * filename
     * file size
     * piece size
     * <p>
     * compute:
     * piece number
     *
     * @throws IOException
     */
    void readCommon() throws IOException {
        File file = new File("Common.cfg");
        InputStreamReader reader =
                new InputStreamReader(new FileInputStream(file));
        BufferedReader br = new BufferedReader(reader);


        NumberOfPreferedNeighbors =
                Integer.parseInt(br.readLine().split(" ")[1]);

        UnchokinInterval =
                Integer.parseInt(br.readLine().split(" ")[1]);

        OptUnchokingInterval =
                Integer.parseInt(br.readLine().split(" ")[1]);

        FileName =
                br.readLine().split(" ")[1];

        FileSize =
                Integer.parseInt(br.readLine().split(" ")[1]);

        PieceSize =
                Integer.parseInt(br.readLine().split(" ")[1]);
        pieceNum = FileSize/PieceSize;

        if (FileSize%PieceSize != 0) {
            remainPieceSize = FileSize%PieceSize;
            pieceNum++;
        } else {
            remainPieceSize = PieceSize;
        }

    }

    /**
     * read peers.cfg
     * create peers info list
     *
     * @throws IOException
     */
    void readPeerInfo(int pid) throws IOException {
        File file = new File("PeerInfo.cfg");
        InputStreamReader reader =
                new InputStreamReader(new FileInputStream(file));
        BufferedReader br = new BufferedReader(reader);
        String temp = br.readLine();
        int myIndex = 0;
        while (!temp.isEmpty()) {
            String[] str = temp.split(" ");
            int pidTemp = 0;
            try {
                pidTemp = Integer.parseInt(str[0]);
            } catch (NumberFormatException e) {
                System.out.print("invalid pid. " + str[0]);
                e.printStackTrace();
            }

            if (pid != pidTemp) {
                Peer ptemp = new Peer(str);
                peers.add(ptemp);
                temp = br.readLine();
            } else {
                myAddr = str[1];
                myPort = Integer.parseInt(str[2]);
                myFile = Integer.parseInt(str[3]) != 0;
                myBitField = new BitField(myFile, pieceNum, 1);
                this.myIndex = myIndex;
            }
            myIndex++;
        }
    }

    public int getNumberOfPreferedNeighbors() {
        return NumberOfPreferedNeighbors;
    }

    public int getUnchokinInterval() {
        return UnchokinInterval;
    }

    public int getOptUnchokingInterval() {
        return OptUnchokingInterval;
    }

    public String getFileName() {
        return FileName;
    }

    public int getFileSize() {
        return FileSize;
    }

    public int getPieceSize() {
        return PieceSize;
    }

    /*public ArrayList<Peer> getPeers() {
        return peers;
    }*/

    public int getPieceNum() {
        return pieceNum;
    }

    public int getRemainPieceSize() {
        return remainPieceSize;
    }

    public int getMyPid() {
        return myPid;
    }

    public String getMyAddr() {
        return myAddr;
    }

    public int getMyPort() {
        return myPort;
    }

    public Boolean getMyFile() {
        return myFile;
    }

    public BitField getMyBitField() {
        return myBitField;
    }

    public int getMyIndex() {
        return myIndex;
    }

    public ArrayList<Peer> getPeers() {
        return peers;
    }

    class Peer {
        int PID;
        BitField bitField;
        Boolean chokeMe;
        Boolean choked;
        Boolean interestMe;
        Boolean interested;
        int transRate;
        Boolean haveFile;
        String address;
        int port;
        Socket socket;

        public Peer(String[] peerInfo) {
            if (peerInfo.length == 4) {
                try {
                    PID = Integer.parseInt(peerInfo[0]);
                } catch (NumberFormatException e) {
                    System.out.print("invalid pid." + peerInfo[0]);
                    e.printStackTrace();
                }
                bitField = new BitField(new byte[0]);
                chokeMe = true;
                choked = true;
                interestMe = false;
                interested = false;
                transRate = 0;
                address = peerInfo[1];
                try {
                    port = Integer.parseInt(peerInfo[2]);
                } catch (NumberFormatException e) {
                    System.out.print("invalid port" + peerInfo[2]);
                    e.printStackTrace();
                }
                /*try {
                    socket = new Socket(peerInfo[1], port);
                } catch (IOException e) {
                    System.out.print("failed to create socket to peer " + PID + ", address: " + peerInfo[1] + ", port: " + port);
                    e.printStackTrace();
                }*/
                int haveFile = 0;
                try {
                    haveFile = Integer.parseInt(peerInfo[3]);
                } catch (NumberFormatException e) {
                    System.out.print("invalid have file info: " + peerInfo[3]);
                    e.printStackTrace();
                }
                this.haveFile = haveFile == 1;
                /*if (haveFile == 1){
                    this.haveFile = true;
                } else if (haveFile == 0) {
                    this.haveFile = false;
                } else {
                    try {
                        throw new Exception();
                    } catch (Exception e) {
                        System.out.print("invalid have file info: " + haveFile);
                        e.printStackTrace();
                    }
                }*/
                bitField = new BitField(this.haveFile, pieceNum);
            }
        }

        public int getPID() {
            return PID;
        }

        public void setPID(int PID) {
            this.PID = PID;
        }

        public BitField getBitField() {
            return bitField;
        }

        public void setBitField(BitField bitField) {
            this.bitField = bitField;
        }

        public Boolean getChokeMe() {
            return chokeMe;
        }

        public void setChokeMe(Boolean chokeMe) {
            this.chokeMe = chokeMe;
        }

        public Boolean getChoked() {
            return choked;
        }

        public void setChoked(Boolean choked) {
            this.choked = choked;
        }

        public Boolean getInterestMe() {
            return interestMe;
        }

        public void setInterestMe(Boolean interestMe) {
            this.interestMe = interestMe;
        }

        public Boolean getInterested() {
            return interested;
        }

        public void setInterested(Boolean interested) {
            this.interested = interested;
        }

        public int getTransRate() {
            return transRate;
        }

        public void setTransRate(int transRate) {
            this.transRate = transRate;
        }

        public Boolean getHaveFile() {
            return haveFile;
        }

        public void setHaveFile(Boolean haveFile) {
            this.haveFile = haveFile;
        }

        public Socket getSocket() {
            return socket;
        }

        public void setSocket() throws IOException {
            socket = new Socket(address,port);
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
        }

        public String getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }
    }

}

/*
class PeerInfo {
    int ID;
    String IP;
    int port;
    boolean haveFile;

    PeerInfo(String[] strings) {
        ID = Integer.parseInt(strings[0]);
        IP = strings[1];
        port = Integer.parseInt(strings[2]);
        haveFile = Integer.parseInt(strings[3]) == 1 ? true : false;
    }
}*/
