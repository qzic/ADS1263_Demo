package ca.qzic.ads1263test_py.network.Common;

// change project to groupId
import static ca.qzic.net.Common.NetCommon.*;
//

import static ca.qzic.ads1263test_py.network.AppMsg.sendAppMsg;
import static java.lang.System.*;
import java.io.*;
import static java.time.LocalTime.now;

/**
 * @author Quentin
 */
public class AppCommon {
    public static final boolean debugAPP = false;
    // This should be generated once and is app dependant
    public static final String uuidString = "15c6093b-0000-1000-8000-00805f9b34fb";
    public static final String platform = "RPi-5-1";
    public static final String ipAddress = "192.168.1.168";
    
    // We never use the ordinal number in msgs only the msgCodeByte
    public static enum AppMsgCodes {
        UNKNOWN, STRING, MY_IP, STOP, DIE;
        private byte msgCodeByte;
        public byte getMsgCodeByte() {
            return msgCodeByte;
        }
        private AppMsgCodes() {
            this.msgCodeByte = (byte) this.ordinal();
        }
        public static AppMsgCodes getMsgCode(byte b) {
            AppMsgCodes[] bmcs = AppMsgCodes.values();
            for (AppMsgCodes appMsgCode : bmcs) {
                if(b == appMsgCode.getMsgCodeByte()) {
                    return appMsgCode;
                }
            }
            return AppCommon.AppMsgCodes.UNKNOWN;
        }
    };

// **** GENERAL MESSAGE FORMAT ****
    
//    +-------------------------+
//    |       APP/NET           |    Application or network level message
//    +-------------------------+
//    |       MSG CODE          |    Type of message
//    +-------------------------+
//    |    Payload LENGTH       | 
//    |      (optional)         |    STRING messages need to specify string length
//    |    or just Paylosd      |
//    |      (optional)         |    Not all message need more data than the MSG CODE
//    +- - - - - - - - - - - - -+
//    +-------------------------+

    public static final int LENGTH_OFFSET = 2;
    public static final int MSG_HDR_SIZE = 3;   // The ablove is not considred part of the header!!


    
    //==========================================================================================
        public static boolean send(AppMsgCodes msgCode) {
        byte[] msg = new byte[2];
        msg[0] = Layers.APP.getMsgCodeByte();
        msg[1] = msgCode.getMsgCodeByte();
        return sendMsg(msg);
    }

//    public static boolean send(AppMsgCodes msgCode, AppMsgCodes lastMsgCode) {
//        byte[] msg = new byte[3];
//        msg[0] = Layers.APP.getMsgCodeByte();
//        msg[1] = msgCode.getMsgCodeByte();
//        msg[2] = lastMsgCode.getMsgCodeByte();
//        return sendMsg(msg);
//    }
//
//    public static boolean send(AppMsgCodes msgCode, byte msgPayload) {
//        byte[] msg = new byte[3];
//        msg[0] = Layers.APP.getMsgCodeByte();
//        msg[1] = msgCode.getMsgCodeByte();
//        msg[2] = msgPayload;
//        return sendMsg(msg);
//    }
//
//    public static boolean send(AppMsgCodes msgCode, Type type) {
//        byte[] msg = new byte[3];
//        msg[0] = Layers.APP.getMsgCodeByte();
//        msg[1] = msgCode.getMsgCodeByte();
//        msg[2] = type.getMsgCodeByte();
//        return sendMsg(msg);
//    }
//
//    public static boolean send(AppMsgCodes msgCode, Type type, byte data1) {
//        byte[] msg = new byte[4];
//        msg[0] = Layers.APP.getMsgCodeByte();
//        msg[1] = msgCode.getMsgCodeByte();
//        msg[2] = type.getMsgCodeByte();
//        msg[3] = data1;
//        return sendMsg(msg);
//    }
//
//    public static boolean send(AppMsgCodes msgCode, Type type, byte data1, byte data2) {
//        byte[] msg = new byte[5];
//        msg[0] = Layers.APP.getMsgCodeByte();
//        msg[1] = msgCode.getMsgCodeByte();
//        msg[2] = type.getMsgCodeByte();
//        msg[3] = data1;
//        msg[4] = data2;
//        return sendMsg(msg);
//    }
//
//    public static boolean send(AppMsgCodes msgCode, byte[] msgPayload) {
//        byte[] msg = new byte[msgPayload.length + 2];
//        msg[0] = Layers.APP.getMsgCodeByte();
//        msg[1] = msgCode.getMsgCodeByte();
//        System.arraycopy(msgPayload, 0, msg, 2, msgPayload.length); // write serialized object into msg
//        return sendMsg(msg);
//    }
    
    public static boolean send(AppMsgCodes msgCode, String msgPayload) {
        int length = msgPayload.length();
        byte[] msg = new byte[length + MSG_HDR_SIZE];
        msg[0] = Layers.APP.getMsgCodeByte();
        msg[1] = msgCode.getMsgCodeByte();
        msg[2] = (byte) length;
        System.arraycopy(msgPayload.getBytes(), 0, msg, MSG_HDR_SIZE, length);
        return sendMsg(msg);
    }

    public static boolean sendMsg(byte[] msg){
        AppMsgCodes appMsgCode = AppMsgCodes.getMsgCode(msg[MSG_CODE_OFFSET]);
        if (debugAPP) {
            out.println("App sending msgCode = " + appMsgCode + ", " + appMsgCode.getMsgCodeByte() + ", " + now());
        }
        return sendAppMsg(msg);
    }
    //==========================================================================================

    //------------------------------------------------------------------------------------------
    public static Object receiveSharedData(byte[] msgBytes) throws IOException {
        byte[] objData = new byte[msgBytes.length - MSG_HDR_SIZE];
        System.arraycopy(msgBytes, MSG_HDR_SIZE, objData, 0, objData.length);
        return deserialize(objData);
    }

    //------------------------------------------------------------------------------------------
    public static final class GlobalSharedData implements java.io.Serializable {
        private static final long serialVersionUID = 12345678912L;
        public int data1;
        public int data2;

        public GlobalSharedData() {
            data1 = 0; 
            data2 = 255; 
        }
    }

}
