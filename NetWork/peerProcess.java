package NetWork;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.*;

import static NetWork.ActualMsg.*;
import static NetWork.ConstantMethod.*;
import static NetWork.ManageFile.*;

/**
 * Created by zhupd on 2/18/2017.
 */
public class peerProcess {

    static Config config;
    //PeerState peerState=new PeerState();
    static ManageFile fileManager;
    private HandShakeMsg handshake;
    //private int myID;
    //int guestID;
    private static ArrayList<Config.Peer> neighbourPeers;
    static Logger logger;
    /*final byte[] chokeMsg = {0,0,0,1,0};
    final byte[] unchokeMsg = {0,0,0,1,1};
    final byte[] interestedMsg = {0,0,0,1,2};
    final byte[] notInterestedMsg = {0,0,0,1,3};

    byte[] haveMsg = {0,0,0,5,4};
    byte[] requstMsg = {0,0,0,5,6};*/

    peerProcess(int pid) throws IOException {
        //myID = pid;
        config = new Config(pid);
        fileManager = new ManageFile(config);
        handshake = new HandShakeMsg(pid);
        neighbourPeers = new ArrayList<>();
        logger = new Logger(config);
    }

    public static ArrayList<Config.Peer> getNeighbourPeers() {
        return neighbourPeers;
    }

    public static synchronized void setNeighbourPeers(ArrayList<Config.Peer> neighbour) {
        neighbourPeers = neighbour;
    }

    /**
     * inputstream, read by fixed byte, first read message length,
     * then message type
     * then payload(defined by length)
     */
    void run() {
        //PeerInfo peer = config.peerInfo.get(guestID);
        //Send handshake message to the peers whose pid less than me
        try {
            for (int i = 0; i < config.getMyIndex(); i++) {
                sendHandShake(config.getPeers().get(i));
                //System.out.println("sent shakemsg to" + "i");
            }
            //when I'm not the last one in the PeerInfo.cfg
            ServerSocket serverSocket = new ServerSocket(config.getMyPort());
            if (config.getMyIndex() < config.getPeers().size()) {
                //ServerSocket serverSocket = new ServerSocket(config.getMyPort());
                //Waiting for the handshake message from the peers whose pid greater than me
                for (int i = config.getMyIndex(); i < config.getPeers().size(); i++) {
                    waitHandshake(config.getPeers().get(i), serverSocket);
                }
                //serverSocket.close();
            }

            // send bitfield message and interest message
            // initialize the interest flags
            for (Config.Peer peer : config.getPeers()
                    ) {
                ActualMsg bitfieldMsg = new ActualMsg(config.getMyBitField());

                if (config.getMyFile()) {
                    //if I have the whole file, send the bitfield message
                    bitfieldMsg.sendActualMsg(peer.getSocket().getOutputStream());
                    //wait for an interest message and then set the flag of this peer
                    //may add it into an interest list
                    //readActualMsg(peer.getSocket());
                    peer.setInterestMe(true);
                    peer.setInterested(false);
                }

                if (peer.getHaveFile()) {
                    //if this peer have whole file, read and log the bitfield message
                    //then send interest message
                    readActualMsg(peer.getSocket());
                    ActualMsg interestMsg = new ActualMsg(MsgType.INTERESTED);
                    interestMsg.sendActualMsg(peer.getSocket().getOutputStream());
                    peer.setInterestMe(false);
                    peer.setInterested(true);
                    //when the thread of this peer start, the first thing to do is waiting for the unchoke message.
                }
            }
            //start peerThread here
            ExecutorService peerThreadPool = Executors.newFixedThreadPool(config.getPeers().size());
            for (Config.Peer peers : config.getPeers()) {
                peerThreadPool.submit(new PeerThread(peers));
            }

            //start optimistic peer unchoke thread and prefered peers unchoke thread here
            ExecutorService specialNeighbourSelector = Executors.newFixedThreadPool(2);
            specialNeighbourSelector.submit(new OptNeighbor());
            specialNeighbourSelector.submit(new PickPreferNeighbour());
            //when finished download, thread exit
            while(!peerThreadPool.isTerminated())
                ;
            specialNeighbourSelector.shutdown();

            //close all Sockets and files
            //server socket has closed after hand shake.
            fileManager.closeManageFile();
            for (Config.Peer peer : config.getPeers()) {
                peer.getSocket().close();
            }
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendHandShake(Config.Peer peer) throws Exception {
        peer.setSocket();
        if (peer.getSocket().isConnected()) {
            //System.out.println("socket:"+peer.getSocket().toString());
            logger.connection(peer.getPID());
        }
        peer.getSocket().setKeepAlive(true);
        handshake.sendMsg(peer.getSocket().getOutputStream());
        if (handshake.readMsg(peer.getSocket()) != peer.getPID()) {
            throw new Exception("Error occurs on hand shaking");
        }
    }

    private void waitHandshake(Config.Peer peer, ServerSocket serverSocket) throws Exception {
        while(!peer.setSocket(serverSocket.accept()))
            ;
        logger.connection(peer.getPID());
        peer.getSocket().setKeepAlive(true);
        handshake.readMsg(peer.getSocket());
        handshake.sendMsg(peer.getSocket().getOutputStream());
    }

}

