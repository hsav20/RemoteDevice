package ltd.kcdevice.device;


import ltd.kcdevice.base.FwbType;

import static main.MAPI.MERROR;
import static main.MAPI.MSTRING;


public class FwbProtoc {
    public byte[] SendData;
    public byte[] RecvdData;

//    private byte[] gRxFwbData;
    private int gRxStatus;										    // 接收到的状态
    private int gRxLength;										    // 接收到的长度
    private int gCrcSum;											// 实时计算的CrcSum


    // [0]传输包同步头0[1]传输包同步头1[2]传输包校验码[3]传输包计数器，一般每包加一，以区分重发包
// [4]传输包指令类型[5]传输包子类型[6-7]传输包长度[8]传输包数据


    public FwbProtoc(){
        RecvdData = new byte[FwbType.FWB_MAX_DATA];
        SendData = new byte[FwbType.FWB_MAX_DATA];
        SendData[0] = FwbType.FWB_SYNC_0;
        SendData[1] = FwbType.FWB_SYNC_1;
    }
    public boolean recvByte(byte data8){		// 接收一包返回0
        RecvdData[gRxStatus] = data8;
        if (++gRxStatus == 1){								// [0]SYNC0
            if (data8 != FwbType.FWB_SYNC_0){
                gRxStatus = 0;
                return false;
            }
        }else if (gRxStatus == 2){							// [1]SYNC1
            if (data8 != FwbType.FWB_SYNC_1){
                gRxStatus = 0;
                return false;
            }
            gCrcSum = 0;
        }else if (gRxStatus <= 8){							// [2]CRCSUM[3]TYPE[4:5]LENGTH
            if (gRxStatus > 3){
                gCrcSum += data8;
            }
            if (gRxStatus == 8){							// 长度高位B15:8
                int length = ((RecvdData[7]&0xff) << 8) | (RecvdData[6]&0xff);
//                MSTRING(String.format("FwbProtoc recvByte length %s",length));
                if (length <= (FwbType.FWB_MAX_DATA-8)){		// 正常长度
                    gRxLength = (8 + length);
                    //	MLOG("FwbRecvByte B %02x %02x %02x", protoc->CrcSum, protoc->CmdTyp, protoc->Length);
                    if (length == 0){						    // 只有协议头，没有内容的情况
                        if (RecvdData[2] == (byte)(gCrcSum&0xff)){					// 传输包正确
                            gRxStatus = 0;
                            return true;
                        }else {
                            MERROR(String.format("RunBle fwbRecvByteA %02x %02x %02x", gRxLength,RecvdData[2], (byte)(gCrcSum&0xff)));
                        }
                        gRxStatus = 0;
                    }
                }else {												// 不正常的长度
                    gRxStatus = 0;
                }
            }
        }else {
            gCrcSum += data8;
            if (gRxStatus == gRxLength){
                if (RecvdData[2] == (byte)(gCrcSum&0xff)){					// 传输包正确
                    MSTRING(String.format("FwbProtoc RecvdData[2] == (byte)(gCrcSum&0xff)"));
                    gRxStatus = 0;
                    return true;
                }else {
                    MERROR(String.format("RunBle fwbRecvByteB %02x %02x %02x", gRxLength,RecvdData[2], (byte)(gCrcSum&0xff)));
                }
                gRxStatus = 0;
            }
        }
        return false;
    }

    public void fwbWrite(byte cmdTyp, byte[] inData, int offset, int length) {
//        byte[] outData;
//        if (cmdTyp != FwbUser.FWB_CMD_NONE.Value) {
//            outData = make(cmdTyp, inData, offset, length);
//        } else {
//            outData = inData;
//        }
//        if (MKCDEV.mSendFifo == null) {
//            MKCDEV.mSendFifo = new ArrayList<>();
//        }
//        MKCDEV.mSendFifo.add(outData);
    }
//
//
//
//        if (MKCDEV.mFwbProtoc == null){
//            MKCDEV.mFwbProtoc = new FwbProtoc();
//        }
//        int remain = length + 8;
//        offset = 0;
//        while (remain != 0){
//            if (remain > 20){
//                length = 20;
//                remain -= 20;
//            }else {
//                length = remain;
//                remain = 0;
//            }
//            byte[] wrData = new byte[length];
//            System.arraycopy(outData, offset, wrData, 0, length);
//            if (!MKCDEV.mRunBle.writeData(wrData)){
//                return false;
//            }
//            offset += length;
//        }
//        return true;
//    }

    public byte[] makeProtoc(byte cmdTyp, byte[] inData, int offset, int length){
        SendData[3] += 1;
        SendData[4] = cmdTyp;
        SendData[6] = (byte) length;
        SendData[7] = (byte) (length >> 8);
        if (inData != null) {
            System.arraycopy(inData, offset, SendData, 8, length);
        }
        SendData[2] = getCrcSum(SendData, 3, length + 5);
        return SendData;
    }
    public byte getCrcSum(byte[] inData, int offset, int length){
        int crcSum = 0;
        for (int counter = 0; counter < length; counter++){
            crcSum += inData[offset++];
        }
        return (byte) crcSum;
    }
}
