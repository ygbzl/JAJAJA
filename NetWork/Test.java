package NetWork;

import java.net.Socket;

/**
 * Created by zhupd on 4/9/2017.
 */
public class Test {
    public static void main(String[] args) throws Exception {

        peerProcess p = new peerProcess(1002);
        p.run();
        /*byte[] msgLength = new byte[] {0,0,0,1};
        byte msgType = 1;
        byte[] tosend = ConstantMethod.mergeBytes(msgLength, new byte[]{msgType});
        tosend = ConstantMethod.mergeBytes(tosend, new byte[0]);*/
    }
}
