package NetWork;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by leqi on 2017/4/7.
 */
public class PeerThread implements Runnable {
    //ActualMsg receiver;
    //Config config;
    //ManageFile fileManager;
    private Config.Peer guestPeer;
    //private int pid;
    //private int peerIndex;
    private Random random;
    private ArrayList<Config.Peer> peerArrayList;

    PeerThread(Config.Peer peer) {
        //receiver = new ActualMsg();
        //this.config = config;
        //this.fileManager = fileManager;
        guestPeer = peer;
        peerArrayList = peerProcess.config.getPeers();
        //pid = peer.getPID();
        //peerIndex = index;
        random = new Random(System.currentTimeMillis());
    }

    @Override
    public void run() {
        //peerProcess.config;
        //peerProcess.fileManager;

        //I don't have file
        boolean t = true;
        try {
            while (t) {
                ActualMsg temp = ActualMsg.readActualMsg(guestPeer.getSocket().getInputStream());
                switch (temp.getType()) {
                    case CHOKE:
                        //received choke message, set flag chokeMe.
                        guestPeer.setChokeMe(true);
                        break;

                    case UNCHOKE:
                        //received unchoke message, set flag chokeMe, send request message.
                        guestPeer.setChokeMe(false);
                        if (guestPeer.getInterested()) {
                            ActualMsg request = new ActualMsg(ActualMsg.MsgType.REQUEST, guestPeer.getBitField().randomSelectIndex(random));
                            request.sendActualMsg(guestPeer.getSocket().getOutputStream());
                        }
                        break;

                    case INTERESTED:
                        //received interest message, set flag interestMe.
                        guestPeer.setInterestMe(true);
                        break;

                    case NOTINTERESTED:
                        //received not interest message, set flag interestMe.
                        guestPeer.setInterestMe(false);
                        break;

                    case HAVE:
                        //received have message, update peer's bitfield,
                        // check my bitfield, set flag interested and send interest message if needed.
                        // if interested in this peer and not been choked by this peer, send request.
                        if (peerProcess.config.getMyBitField().isInterested(temp.getIndex())) {
                            //if I am interested in this piece, set flag, send interest message.
                            guestPeer.setInterested(true);
                            ActualMsg interest = new ActualMsg(ActualMsg.MsgType.INTERESTED);
                            interest.sendActualMsg(guestPeer.getSocket().getOutputStream());
                            guestPeer.getBitField().setPiece(temp.getIndex());
                            guestPeer.getBitField().setInterest(temp.getIndex());

                            if (!guestPeer.getChokeMe() && guestPeer.getInterested()) {
                                ActualMsg have_request = new ActualMsg(ActualMsg.MsgType.REQUEST, guestPeer.getBitField().randomSelectIndex(random));
                                have_request.sendActualMsg(guestPeer.getSocket().getOutputStream());
                            }
                        } else {
                            guestPeer.setInterested(false);
                            ActualMsg notInterest = new ActualMsg(ActualMsg.MsgType.NOTINTERESTED);
                            notInterest.sendActualMsg(guestPeer.getSocket().getOutputStream());
                            guestPeer.getBitField().setPiece(temp.getIndex());
                        }
                        break;

                    case BITFIELD:
                        //nothing to do here
                        break;

                    case REQUEST:
                        //check if this peer is choked
                        if (!guestPeer.getChoked()) {
                            //if not choked, then send the piece which is requested.
                            ActualMsg piece = new ActualMsg(peerProcess.fileManager.readMsg(temp.getIndex()));
                            piece.sendActualMsg(guestPeer.getSocket().getOutputStream());
                            guestPeer.incTransNumber();
                        } else {
                            //if choked, just ignore this request.
                            break;
                        }
                        break;

                    case PIECE:
                        // received piece message, write the piece into file,
                        // remove the index of piece from interestlist of my bitfield,
                        // remove the index of piece from interestlist of ALL peers.
                        // check if I am still interested in this peer.
                        // if interest and not be chocked, send another request.
                        // tell everyone I have this piece.
                        peerProcess.fileManager.writeMsg(new FilePiece(temp));
                        peerProcess.config.getMyBitField().removeInterest(temp.getIndex());
                        peerProcess.config.getMyBitField().setPiece(temp.getIndex());

                        ActualMsg reply_have = new ActualMsg(ActualMsg.MsgType.HAVE, temp.getIndex());
                        for (Config.Peer peer:peerArrayList) {
                            peer.getBitField().removeInterest(temp.getIndex());
                            peer.setInterested(peer.getBitField().isInterested());
                            reply_have.sendActualMsg(peer.getSocket().getOutputStream());
                        }

                        /*for (int i = 0; i < peerArrayList.size(); i++) {
                            peerArrayList.get(i).getBitField().removeInterest(temp.getIndex());
                            peerArrayList.get(i).setInterested(peerArrayList.get(i).getBitField().isInterested());
                            reply_have.sendActualMsg(peerArrayList.get(i).getSocket().getOutputStream());
                        }*/

                        if (guestPeer.getInterested() && guestPeer.getChoked()) {
                            //if still interest this peer and not been choked
                            ActualMsg reply_request = new ActualMsg(ActualMsg.MsgType.REQUEST, guestPeer.getBitField().randomSelectIndex(random));
                            reply_request.sendActualMsg(guestPeer.getSocket().getOutputStream());
                        }

                        break;
                }

                if (peerProcess.config.getMyFile()) {
                    t = false;
                    for (Config.Peer peer : peerArrayList
                            ) {
                        if (!peer.getHaveFile()) {
                            t = true;
                        }
                    }
                }

            }
            //guestPeer.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
