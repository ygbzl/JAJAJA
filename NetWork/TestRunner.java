package NetWork;

/**
 * Created by leqi on 2017/4/9.
 */
public class TestRunner {
    public static void main(String[] args) throws Exception {
        peerProcess p1 = new peerProcess(1001);
        //peerProcess p2 = new peerProcess(1002);
        //peerProcess p3 = new peerProcess(1003);

        p1.run();
        //p2.run();
        //p3.run();
    }
}
