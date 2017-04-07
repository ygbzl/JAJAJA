package NetWork;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static NetWork.ActualMsg.*;
import static NetWork.ConstantMethod.*;
import static NetWork.ManageFile.*;

/**
 * Created by zhupd on 2/18/2017.
 */
public class peerProcess  {

    public Config config;
    PeerState peerState=new PeerState();
    public ManageFile fileManager;
    HandShakeMsg handshake;
    int myID;
    int guestID;

    final byte[] chockMsg = {0,0,0,1,0};
    final byte[] unchockMsg = {0,0,0,1,1};
    final byte[] interestedMsg = {0,0,0,1,2};
    final byte[] notInterestedMsg = {0,0,0,1,3};

    byte[] haveMsg = {0,0,0,5,4};
    byte[] requstMsg = {0,0,0,5,6};

    peerProcess(int pid) throws IOException {
        myID = pid;
        config = new Config(myID);
        fileManager = new ManageFile(config);
        handshake = new HandShakeMsg(myID);
    }

    /**
     * inputstream, read by fixed byte, first read message length,
     * then message type
     * then payload(defined by length)
     *
     */
    void run() throws Exception {
        //PeerInfo peer = config.peerInfo.get(guestID);
        //Send handshake message to the peers whose pid less than me
        for (int i = 0; i < config.getMyIndex(); i++) {
            sendHandShake(config.getPeers().get(i));
        }
        //when I'm not the last one in the PeerInfo.cfg
        if (config.getMyIndex() != config.getPeers().size()) {
            ServerSocket serverSocket = new ServerSocket(config.getMyPort());
            //Waiting for the handshake message from the peers whose pid greater than me
            for (int i = config.getMyIndex(); i < config.getPeers().size(); i++) {
                waitHandshake(config.getPeers().get(i), serverSocket);
            }
        }

        // send bitfield message and interest message
        // initialize the interest flags
        for (Config.Peer peer: config.getPeers()
             ) {
            ActualMsg bitfieldMsg = new ActualMsg(config.getMyBitField());

            if (config.getMyFile()) {
                //if I have the whole file, send the bitfield message
                bitfieldMsg.sendActualMsg(peer.getSocket().getOutputStream());
                //wait for an interest message and then set the flag of this peer
                //may add it into an interest list
                bitfieldMsg.readActualMsg(peer.getSocket().getInputStream());
                peer.setInterestMe(true);
                peer.setInterested(false);
            }

            if (peer.getHaveFile()){
                //if this peer have whole file, read and log the bitfield message
                //then send interest message
                bitfieldMsg.readActualMsg(peer.getSocket().getInputStream());
                ActualMsg interestMsg = new ActualMsg(MsgType.INTERESTED);
                interestMsg.sendActualMsg(peer.getSocket().getOutputStream());
                peer.setInterestMe(false);
                peer.setInterested(true);
                //when the thread of this peer start, the first thing to do is waiting for the unchock message.
            }
        }

        //start optimistic peer unchock thread and prefered peers unchock thread here


        //if I don't have file, begin download
        if (!config.getMyFile()){

        }
        //I have file or download finished begin upload


        //when finished upload, thread exit

        /*while (true) {
            byte[] msgLength = new byte[4];
            byte[] msgType=new byte[1];
            //in.read(msgLength);
            //in.read(msgType);

            //if()
        }*/


    }

    private void sendHandShake(Config.Peer peer) throws Exception {
        peer.setSocket();
        handshake.sendMsg(peer.getSocket().getOutputStream());
        if (handshake.readMsg(peer.getSocket().getInputStream()) != peer.getPID()){
            throw new Exception("Error occurs on hand shaking");
        }
        /*ActualMsg bitfieldMsg = new ActualMsg(config.getMyBitField());

        if (config.getMyFile()) {
            //if I have the whole file, send the bitfield message
            bitfieldMsg.sendActualMsg(peer.getSocket().getOutputStream());
            //wait for an interest message and then set the flag of this peer
            //may add it into an interest list
            bitfieldMsg.readActualMsg(peer.getSocket().getInputStream());
            peer.setInterestMe(true);
            peer.setInterested(false);
        }

        if (peer.getHaveFile()){
            //if this peer have whole file, read and log the bitfield message
            //then send interest message
            bitfieldMsg.readActualMsg(peer.getSocket().getInputStream());
            ActualMsg interestMsg = new ActualMsg(MsgType.INTERESTED);
            interestMsg.sendActualMsg(peer.getSocket().getOutputStream());
            peer.setInterestMe(false);
            peer.setInterested(true);
            //when the thread of this peer start, the first thing to do is waiting for the unchock message.
        }*/

    }

    private void waitHandshake(Config.Peer peer, ServerSocket serverSocket) throws Exception {
        peer.setSocket(serverSocket.accept());
        handshake.readMsg(peer.getSocket().getInputStream());
        handshake.sendMsg(peer.getSocket().getOutputStream());

        /*ActualMsg bitfieldMsg = new ActualMsg(config.getMyBitField());

        if (config.getMyFile()) {
            //if I have the whole file, send the bitfield message
            bitfieldMsg.sendActualMsg(peer.getSocket().getOutputStream());
            //wait for an interest message and then set the flag of this peer
            //may add it into an interest list
            bitfieldMsg.readActualMsg(peer.getSocket().getInputStream());
            peer.setInterestMe(true);
            peer.setInterested(false);
        }

        if (peer.getHaveFile()){
            //if this peer have whole file, read and log the bitfield message
            //then send interest message
            bitfieldMsg.readActualMsg(peer.getSocket().getInputStream());
            ActualMsg interestMsg = new ActualMsg(MsgType.INTERESTED);
            interestMsg.sendActualMsg(peer.getSocket().getOutputStream());
            peer.setInterestMe(false);
            peer.setInterested(true);
            //when the thread of this peer start, the first thing to do is waiting for the unchock message.
        }*/
    }

    /**
     * according to different type of the Actual message
     * handle the message
     * @param msg
     * @param out
     * @throws IOException
     */
    void receive(ActualMsg msg, OutputStream out) throws IOException {
        MsgType type=msg.getType();
        switch (type) {
            case HAVE: {
                if(peerState.stateMap.get(guestID).isInterested(msg.getIndex())) {
                    //if I don't have this piece
                    //add the index of this piece into the interest list
                    //send interested message
                    //peerState.stateMap.get(guestID).addInterest(msg.getIndex());
                    out.write(interestedMsg);
                }
                peerState.stateMap.get(guestID).updateBitField(msg);
                if(!peerState.stateMap.get(myID).compareBitfield(peerState.stateMap.get(guestID).getBitField())) {
                    //if bitfield has been updated, and I still not interested in any piece
                    //send not interested message to ALL not interest peers
                    //TODO
                    out.write(notInterestedMsg);
                }
            }

            break;

            case BITFIELD: {
                //we could get all bitfield info when reading the PeerInfo.cfg
                //so we simply ignore this message
            }

            break;

            case INTERESTED: {
                //set interested to true
                peerState.stateMap.get(guestID).setInterested(true);
            }

            break;

            case NOTINTERESTED: {
                //set interested to false
                peerState.stateMap.get(guestID).setInterested(false);
            }

            break;

            case CHOKE: {
                //set choke to true
                peerState.stateMap.get(guestID).setChoke(true);
            }

            break;

            case UNCHOKE: {
                //set choke to false
                peerState.stateMap.get(guestID).setChoke(false);
                //randomly select an index from interest list
                //send a request Message
                int index = peerState.stateMap.get(guestID).randomSelectIndex();
                out.write(mergeBytes(requstMsg, intToBytes(index)));
            }

            break;

            case REQUEST: {
                //request message
                //fetch the index from message
                //send the piece the peer want
                int index = msg.getIndex();
                //out.write(writePieceMsg(intToBytes(index)));
            }

            break;

            case PIECE: {
                //read message
                //readPieceMsg(msg);
                //update my bitfield
                peerState.stateMap.get(myID).updateBitField(msg);
                //send HAVE message to ALL peers
                //TODO
                out.write(mergeBytes(haveMsg, intToBytes(msg.getIndex())));

            }

            break;

        }//end switch
    }

}

