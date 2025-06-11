package ca.qzic.ads1263test_py.network;

// change project to groupId
import static ca.qzic.net.BlueTooth.NetTxMsg.sendMsgBT;
//

/**
 *
 * @author Quentin
 */
public class AppMsg {
        public static boolean sendAppMsg(byte[] msg) {
        return sendMsgBT(msg);
    }
}
