package NetWork;

/**
 * Created by leqi on 2017/4/10.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        peerProcess p = new peerProcess(Integer.parseInt(args[0]));
        //peerProcess p = new peerProcess(1001);
        p.run();
    }
}
