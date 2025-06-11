/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qzic.ads1263test_py.network.Networks;

// change project to groupId
import ca.qzic.net.BlueTooth.*;
import static ca.qzic.net.Common.NetCommon.*;
import static ca.qzic.net.IP.NetworkList.findMyIP;
//

import static ca.qzic.ads1263test_py.network.Common.AppCommon.AppMsgCodes.*;
import static ca.qzic.ads1263test_py.network.Common.AppCommon.*;
import static ca.qzic.ads1263test_py.Main.*;

import javax.swing.*;
import java.io.*;

import org.slf4j.*;

/**
 *
 * @author Quentin
 */
public class AppMsgHandler implements MsgHandlerIF {
    private Logger logger = LoggerFactory.getLogger(AppMsgHandler.class);
    private final MsgHandler en;
    Layers layer;
    AppMsgCodes appMsgCode;
    BtMsgCodes btMsgCode;
    int length;

    public AppMsgHandler(String uuid) {
        layer = Layers.UNKNOWN;
        appMsgCode = AppMsgCodes.UNKNOWN;
        btMsgCode = BtMsgCodes.UNKNOWN;
        // Create the mesage Handle and pass ourself to it.
        en = new MsgHandler(uuid, this);
    }

    // Define the actual handler for the event.
    @Override
    public void appMsgHandler(byte[] msgBytes) throws IOException {
        layer = Layers.getMsgCode(msgBytes[LAYER_CODE_OFFSET]);
        appMsgCode = AppMsgCodes.getMsgCode(msgBytes[MSG_CODE_OFFSET]);
        btMsgCode = BtMsgCodes.getMsgCode(msgBytes[MSG_CODE_OFFSET]);
        length = msgBytes[LENGTH_OFFSET];
        String msgName;
        boolean sentIP = false;

        if (layer == Layers.APP) {
            if (debugAPP) {
                msgName = appMsgCode.toString();
                if (appMsgCode != AppMsgCodes.UNKNOWN) {
                    logger.error("appMsgHandler msg rcvd = [" + appMsgCode + "]   [" + msgBytes[MSG_CODE_OFFSET] + "]  " + now());
                }
            }
            switch (appMsgCode) {
                case STRING:
                    String s = (new String(msgBytes).substring(MSG_HDR_SIZE,MSG_HDR_SIZE + length));
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            // Here, we can safely update the GUI
                            // because we'll be called from the
                            // event dispatch thread
                            netHost.MessageRecived(s);
                        }
                    });
                    break;

                case STOP:
                    logger.warn("Remote Controller says STOP!");
                    stop = true;
                    break;
                case DIE:
                    logger.warn("Remote Controller says DIE!");
                    die = true;
                    break;
                default:

            }
        }
        if (layer == Layers.BT) {
            if (debugAPP) {
                if (btMsgCode != BtMsgCodes.UNKNOWN) {
                    logger.error("appMsgHandler msg rcvd = [" + btMsgCode + "]   [" + msgBytes[MSG_CODE_OFFSET] + "]  " + now());
                }
            }
            switch (btMsgCode) {
                case BT_CONNECTED:
                    logger.info("BT connected");
                    // Send IP address to comtroller
                    if (!sentIP) {
                        String s = findMyIP() + "\n";
                        send(MY_IP, s);
                        sentIP = true;
                    }
                    break;
                case BT_DISCONNECTED:
                    logger.info("BT disconnected");
                    break;
                default:
            }
        }
    }
}
