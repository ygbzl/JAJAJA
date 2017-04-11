package NetWork;

import java.io.IOException;
import java.io.InputStream;

/**
 * static method which can be called from other class
 * Created by zhupd on 2/20/2017.
 */
class ConstantMethod {



    /**
     * converse byte[] to int
     * @param src
     * @return
     */

    public static int bytesToInt(byte[] src) {
        int value;
        value = (int) ( ((src[0] & 0xFF)<<24)
                |((src[1] & 0xFF)<<16)
                |((src[2] & 0xFF)<<8)
                |(src[3] & 0xFF));
        return value;
    }

    /**
     * converse int int to byte[]
     * @param value
     * @return
     */
    public static byte[] intToBytes(int value)
    {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }


    /**
     * merge byte[] aa, byte[] bb as aa+bb
     * @param aa
     * @param bb
     * @return
     */
    public static byte[] mergeBytes(byte[] aa, byte[] bb) {
        byte[] res = new byte[aa.length + bb.length];
        for(int i=0;i<aa.length;i++) {
            res[i] = aa[i];
        }
        int i=0;
        for(int j=aa.length;j<bb.length+aa.length;j++) {
            res[j]=bb[i++];
        }

        return res;
    }

    /**
     * compare the content of two byte[] instead of compare object
     *
     */

    public static boolean byteArrayComparer(byte[] a, byte[] b) {
        if (a.length == b.length){
            for (int i = 0; i < a.length; i++) {
                if (a[i] != b[i]){
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
