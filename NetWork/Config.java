package NetWork;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zhupd on 2/19/2017.
 */
public class Config {
    private int NumberOfPreferedNeighbors;
    private int UnchokinInterval;
    private int OptUnchokingInterval;
    private String FileName;
    private int FileSize;
    private int PieceSize;
    //HashMap<Integer, Peer> peers;
    private ArrayList<Peer> peers;
    private int pieceNum;
    private int remainPieceSize;

    private int myPid;
    private String myAddr;
    private int myPort;
    private Boolean myFile;
    private BitField myBitField;
    private int myIndex;
    private int totalDownload = 0;
    private boolean iscompleted=false;

    Config(int pid) {
        myPid = pid;
        peers = new ArrayList<>();
        try {
            readCommon();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            readPeerInfo(myPid);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        pieceNum = FileSize / PieceSize;

        if (FileSize % PieceSize != 0) {
            remainPieceSize = FileSize % PieceSize;
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
        while (temp != null) {
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
                //temp = br.readLine();
            } else {
                myAddr = str[1];
                myPort = Integer.parseInt(str[2]);
                myFile = Integer.parseInt(str[3]) != 0;
                myBitField = new BitField(myFile, pieceNum, 1);
                this.myIndex = myIndex;
            }
            myIndex++;
            temp = br.readLine();
        }
    }

    public boolean isIscompleted() {
        return iscompleted;
    }

    public void setIscompleted(boolean iscompleted) {
        this.iscompleted = iscompleted;
    }

    int getNumberOfPreferedNeighbors() {
        return NumberOfPreferedNeighbors;
    }

    int getUnchokinInterval() {
        return UnchokinInterval;
    }

    int getOptUnchokingInterval() {
        return OptUnchokingInterval;
    }

    String getFileName() {
        return FileName;
    }

    int getFileSize() {
        return FileSize;
    }

    int getPieceSize() {
        return PieceSize;
    }


    void setMyFile(Boolean myFile) {
        this.myFile = myFile;
    }

    int getPieceNum() {
        return pieceNum;
    }

    int getRemainPieceSize() {
        return remainPieceSize;
    }

    int getMyPid() {
        return myPid;
    }

    String getMyAddr() {
        return myAddr;
    }

    int getMyPort() {
        return myPort;
    }

    Boolean getMyFile() {
        return myFile;
    }

    BitField getMyBitField() {
        return myBitField;
    }

    int getMyIndex() {
        return myIndex;
    }

    ArrayList<Peer> getPeers() {
        return peers;
    }

    public int getTotalDownload() {
        return totalDownload;
    }

    public void incTotalDownload() {
        totalDownload++;
    }

    class Peer {
        private int PID;
        private BitField bitField;
        private Boolean chokeMe;
        private Boolean choked;
        private Boolean interestMe;
        private Boolean interested;
        private Boolean preferedNeighbor;
        private Boolean optimisticNeighbor;
        private int transNumber;
        private int transRate;
        private Boolean haveFile;
        private String address;
        private int port;
        private Socket socket;

        Peer(String[] peerInfo) {
            if (peerInfo.length == 4) {
                try {
                    PID = Integer.parseInt(peerInfo[0]);
                } catch (NumberFormatException e) {
                    System.out.print("invalid pid." + peerInfo[0]);
                    e.printStackTrace();
                }

                chokeMe = true;
                choked = true;
                interestMe = false;
                interested = false;
                preferedNeighbor = false;
                optimisticNeighbor = false;
                transRate = 0;
                transNumber = 0;
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
                bitField = new BitField(this.haveFile, pieceNum);
            } else {
                try {
                    throw new Exception("invalid peer info");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int getTransNumber() {
            return transNumber;
        }

        void setTransNumber(int transNumber) {
            this.transNumber = transNumber;
        }

        synchronized void incTransNumber() {
            this.transNumber++;
        }

        int getPID() {
            return PID;
        }

        void setPID(int PID) {
            this.PID = PID;
        }

        BitField getBitField() {
            return bitField;
        }

        void setBitField(BitField bitField) {
            this.bitField = bitField;
        }

        Boolean getChokeMe() {
            return chokeMe;
        }

        synchronized void setChokeMe(Boolean chokeMe) {
            this.chokeMe = chokeMe;
        }

        Boolean getChoked() {
            return choked;
        }

        synchronized void setChoked(Boolean choked) {
            this.choked = choked;
        }

        Boolean getInterestMe() {
            return interestMe;
        }

        synchronized void setInterestMe(Boolean interestMe) {
            this.interestMe = interestMe;
        }

        Boolean getInterested() {
            return interested;
        }

        synchronized void setInterested(Boolean interested) {
            this.interested = interested;
        }

        int getTransRate() {
            return transRate;
        }

        void setTransRate(int transRate) {
            this.transRate = transRate;
        }

        Boolean getHaveFile() {
            return haveFile;
        }

        void setHaveFile(Boolean haveFile) {
            this.haveFile = haveFile;
        }

        Socket getSocket() {
            return socket;
        }

        Boolean setSocket() throws IOException {
            socket = new Socket(address, port);
            //while(!socket.isConnected())
            return socket.isConnected();
        }

        Boolean getPreferedNeighbor() {
            return preferedNeighbor;
        }

        void setPreferedNeighbor(Boolean preferedNeighbor) {
            this.preferedNeighbor = preferedNeighbor;
        }

        Boolean getOptimisticNeighbor() {
            return optimisticNeighbor;
        }

        void setOptimisticNeighbor(Boolean optimisticNeighbor) {
            this.optimisticNeighbor = optimisticNeighbor;
        }

        boolean setSocket(Socket socket) {
            if(socket != null) {
                this.socket = socket;
                return true;
            } else {
                return false;
            }

        }

        String getAddress() {
            return address;
        }

        int getPort() {
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
