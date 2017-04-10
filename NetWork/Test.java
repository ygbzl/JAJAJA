package NetWork;

/**
 * Created by zhupd on 4/9/2017.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        int pid = Integer.parseInt(args[0]);
        peerProcess p = new peerProcess(pid);
        p.run();
    }
}
