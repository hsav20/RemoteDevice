package ltd.kcdevice.device;

import java.util.ArrayList;

import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.base.BDevice;
import ltd.kcdevice.base.BTickBytes;
import ltd.kcdevice.base.FwbType;
import ltd.kcdevice.base.Kc3xType;
import ltd.kcdevice.base.SpkArea;
import ltd.kcdevice.base.SpkChannel;
import ltd.kcdevice.base.SpkSetup;
import main.MAPI;

import static main.MAPI.MERROR;
import static main.MAPI.MSTRING;

public class KcmCtrl {

    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    // inData输入: 	多个[0]长度[1]索引[2]数据
    public void updateByte(byte cmdTyp, byte[] inData, int length){ 	// 推送更新的 RmAudCtr寄存器
//if (cmdTyp != FwbType.FWB_AUD_PUSH_REGISTER)return;
        BDevice bDevice = MKCDEV.getRunBDevice();
//        if (MKCDEV.mRunBle != null && MKCDEV.mRunBle.mBDevice != null) {
//            bDevice = MKCDEV.mRunBle.mBDevice;
//        }
        MLOG(String.format("updateByte AAA %02x_%02x_%s", cmdTyp, length, bDevice));
        if (bDevice != null) {
            byte start;
            int size;
            int counter = 8;
            length += 8;                                                    // 加多前面的指令头
            do {
                size = ((inData[counter] & 0xff) - 2);
                start = inData[counter + 1];
            MLOG(String.format("updateByte B [%02x/%02x] %02x %02x %02x", counter, length, size, start, inData[counter+2]));
                if (cmdTyp == FwbType.FWB_AUD_PUSH_RMAUDCTR) {                // 写入/更改音频控制寄存器
                    if (bDevice.RmAudCtrByte != null && (start + size) <= bDevice.RmAudCtrByte.length) {
                        MAPI.COPY_BUFF8(size, counter + 2, inData, start, bDevice.RmAudCtrByte);
//                        MLOG(String.format("FWB_AUD_PUSH_RMAUDCTR updateByteC %02x %02x %02x %02x", start, size, bDevice.RmAudCtrByte.length, bDevice.RmAudCtrByte[Kc3xType.KCM_VOLUME_MAX.Value]));
                    }else {
                        MERROR(String.format("updateByte FwbType.FWB_AUD_PUSH_RMAUDCTR ", bDevice.RmAudCtrByte));
                    }
                } else if (cmdTyp == FwbType.FWB_AUD_PUSH_EQ_VALUE) {            // 写入/更改EQ数值寄存器
                    if (bDevice.EqValueByte != null && (start + size) <= bDevice.EqValueByte.length) {
                        MAPI.COPY_BUFF8(size, counter + 2, inData, start * Kc3xType.RAC_EQVALUE_INDEX_MAX.Value, bDevice.EqValueByte);
                    }else {
                        MERROR(String.format("updateByte FwbType.FWB_AUD_PUSH_EQ_VALUE ", bDevice.EqValueByte));
                    }
//                MLOG(String.format("FWB_AUD_WR_EQ_VALUE C %02x %02x %02x", start, MKCDEV.gRegEqData[start][0], MKCDEV.gRegEqData[start][1]));
                } else {                                                // FWB_AUD_PUSH_PRODUCT 写入/更改产品及网络设置寄存器
//                System.arraycopy(inData, counter+2, MKCDEV.gRegProduct, start, size);
                    if (bDevice.ProductByte != null && (start + size) <= bDevice.ProductByte.length) {
                        MAPI.COPY_BUFF8(size, counter + 2, inData, start, bDevice.ProductByte);
                        MLOG(String.format("FWB_AUD_PUSH_PRODUCT updateByte %02x %02x %02x", start, size, bDevice.ProductByte[ProductInfo.PRODUCT_KCM_MODEL]));
                    }else {
                        MERROR(String.format("updateByte FwbType.FWB_AUD_PUSH_PRODUCT ", bDevice.ProductByte));
                    }
                }

                counter += (2 + size);
//            MLOG(String.format("FWB_AUD_PUSH_REGISTER DDD %02x %02x %02x", counter, inData[counter], length));
            } while (counter < length);
        }
    }

    public int getRmAudCtr(BDevice bDevice, Kc3xType kc3xType) {
        if (bDevice != null) {
            byte[] rmAudCtrByte = bDevice.RmAudCtrByte;
            if (rmAudCtrByte != null) {
                switch (kc3xType) {
                    case RAC_RD_SD_QTY:
                        MLOG(String.format("getRmAudCtr C %02x %02x", kc3xType, rmAudCtrByte[kc3xType.Value]));
                        return getShort(kc3xType.RAC_RD_SD_QTY.Value, rmAudCtrByte);
                    default:
                        break;
                }
//        MLOG(String.format("getRmAudCtr C %02x %02x", kc3xType.Value, MKCDEV.gRegRmAudCtr[kc3xType.Value]));
                return rmAudCtrByte[kc3xType.Value];
            }
        }
        return 0;
    }
    public int getRegEqData(int index, int segment){
        return 0;
    }
    public int getProductValue(int offset){
        return 0;
    }
    public int getProductString(int offset){
        return 0;
    }

    public void wrFifo(byte cmdTyp, byte[]inData, int offset, int length) {
        BTickBytes bTickBytes = new BTickBytes();
        bTickBytes.Tick = System.currentTimeMillis();
        bTickBytes.Data = new byte[3 + length];
        bTickBytes.Data[0] = cmdTyp;
        bTickBytes.Data[1] = (byte) (length & 0xff);
        bTickBytes.Data[2] = (byte) ((length >> 8) & 0xff);
        System.arraycopy(inData, offset, bTickBytes.Data, 3, length);
    }

    public void wrKc3xType(Kc3xType kc3xType, int vaule){               // 写入字节到音频控制寄存器 FWB_WR_REGISTER
        BTickBytes bTickBytes = new BTickBytes();
        bTickBytes.Tick = System.currentTimeMillis();
        int length = 4;
        if (kc3xType.Value >= Kc3xType.RAC_BYTE_REG_END.Value){      // 单字节寄存器结束索引
            length = 5;
        }
        bTickBytes.Data = new byte[length];                         // 必须严格按照要求发送的长度
        bTickBytes.Data[0] = FwbType.FWB_AUD_WR_RMAUDCTR;           // 否则会使用MLOGE打印提示出错了
        bTickBytes.Data[1] = 3;
        bTickBytes.Data[2] = (byte) (kc3xType.Value & 0xff);
        bTickBytes.Data[3] = (byte) (vaule & 0xff);
        if (length > 4) {
            bTickBytes.Data[4] = (byte) ((vaule >> 8) & 0xff);
        }
        if (MKCDEV.mSendFifo == null) {
            MKCDEV.mSendFifo = new ArrayList<>();
        }
        MKCDEV.mSendFifo.add(bTickBytes);
//        MLOG(String.format("wrKc3xType %02x", kc3xType.Value));
    }
//    public void rdRegister(RmAudCtr rmAudCtr){                           // 读取电器的寄存器
//        byte[] data = new byte[1];
//        data[0] = rmAudCtr.Value;
//        fwbWrite(FwbUser.FWB_AUD_RD_RAC.Value, data);
//    }

//    public int getRegister(RmAudCtr rmAudCtr){                           // 读取电器的寄存器
//        if (MKCDEV.gAudioRegister != null){
//            switch (rmAudCtr) {
//                case RAC_PLAY_INDEX:
//                case RAC_PLAY_TIME:
//                    return getShort(rmAudCtr.Value);
//                default:
//                    return MKCDEV.gAudioRegister[rmAudCtr.Value];
//            }
//        }
//        return 0;
//    }

//    public void setInputSource(InputSrc input) {       // KCM_INPUT_SOURCE输入音源选择
//        byte[] data = new byte[2];
//        data[0] = Kc3xType.KCM_INPUT_SOURCE.Value;
//        data[1] = (byte)input.Value;
//        fwbWrite(FwbUser.FWB_AUD_WR_KC3X_TYPE.Value, data);
//
//    }
//    public void setListenMode(ListenMode mode) {     // KCM_LISTEN_MODE聆听模式选择
//        byte[] data = new byte[2];
//        data[0] = Kc3xType.KCM_LISTEN_MODE.Value;
//        data[1] = (byte)mode.Value;
//        fwbWrite(FwbUser.FWB_AUD_WR_KC3X_TYPE.Value, data);
//    }
//    public void setEqSelect(int index) {                    // KCM_EQ_SELECT音效高低音音调或多段EQ均衡器通道选择
//        byte[] data = new byte[2];
//        data[0] = Kc3xType.KCM_EQ_SELECT.Value;
//        data[1] = (byte)(index & 0x03);
//        fwbWrite(FwbUser.FWB_AUD_WR_KC3X_TYPE.Value, data);
//    }
//    public void setVolumeMute(boolean up, boolean down, boolean setMute, boolean unmute) {   // KCM_VOLUME_MUTE音频静音及音量加减控制
//        byte[] data = new byte[2];
//        data[0] = Kc3xType.KCM_VOLUME_MUTE.Value;
//        if (setMute){
//            data[1] = (byte) 0x03;                // B1为控制音频的静音；只有在B1为1时B0才有效; B0为控制整机音频的静音，B0=1静音打开
//        }else if (down){
//            data[1] = (byte) 0x20;                // B5为控制音量的加减；只有在B5为1时B4才有效; B4为1表示音量值加1，B4为0表示音量值减1；
//        }else if (unmute){
//            data[1] = (byte) 0x02;                // B1为控制音频的静音；只有在B1为1时B0才有效; B0为控制整机音频的静音，B0=1静音打开
//        }else if (up){
//            data[1] = (byte) 0x30;                // B5为控制音量的加减；只有在B5为1时B4才有效; B4为1表示音量值加1，B4为0表示音量值减1；
//        }
//        fwbWrite(FwbUser.FWB_AUD_WR_KC3X_TYPE.Value, data);
//    }
//    public boolean setChannelTrim(SpkChannel spkChannel, int value) {    // 设置各声道的微调
//        byte[] data = new byte[2];
//        data[0] = (byte) (Kc3xType.KCM_FL_TRIM.Value + spkChannel.Value);
//        if (value >= 0 && value <= 15){
//            data[0] = (byte) (0x10 | (value & 0x0f));
//        }else if (value < 0 && value >= -15){
//            data[0] = (byte) (0x10 - (value & 0x0f));
//        }else {
//            return false;
//        }
//        fwbWrite(FwbUser.FWB_AUD_WR_KC3X_TYPE.Value, data);
//        return true;
//    }
    public boolean setDelayTime(SpkChannel spkChannel, int time) {           // KCM_DELAY_TIME设置各声道的延迟时间
//        byte[] data = new byte[1 + 8];
//        data[0] = Kc3xType.KCM_DELAY_TIME.Value;
//        if (MKCDEV.gAudioRegister != null){
//            if (time <= MKCDEV.gAudioRegister[RmAudCtr.RAC_DELAY_TIME_MAX.Value + spkChannel.Value]){
//                System.arraycopy(MKCDEV.gAudioRegister, RmAudCtr.RAC_SET_DELAY_TIME.Value, data, 0, 8);
//                MKCDEV.gAudioRegister[RmAudCtr.RAC_SET_DELAY_TIME.Value + spkChannel.Value] = (byte) time;
//                return true;
//            }
//        }
        return false;
    }

//    public void setTestTone(boolean enable, SpkChannel spkChannel) {    // KCM_TEST_TONE噪音测试控制
//        byte[] data = new byte[2];
//        data[0] = Kc3xType.KCM_TEST_TONE.Value;
//        if (enable){
//            data[1] = (byte) (0x10 | (spkChannel.Value & 0x07));
//        }
//        fwbWrite(FwbUser.FWB_AUD_WR_KC3X_TYPE.Value, data);
//    }
//    public void setEqSetup() {                                  // KCM_EQ_SETUP多路均衡EQ音效处理设置
//    }



    public void setSpkConfig(SpkArea spkArea, SpkSetup spkSetup) {     // KCM_SPK_CONFIG喇叭设置
//        if (MKCDEV.gAudioRegister != null) {
//            byte[] data = new byte[2];
//            data[0] = Kc3xType.KCM_SPK_CONFIG.Value;
//            data[1] = MKCDEV.gAudioRegister[RmAudCtr.RAC_SPK_CONFIG.Value];
//            switch (spkArea){
//                case SUBWOOFER:
//                    if (spkSetup != SpkSetup.NONE){
//                        data[1] |= 0x01;                                 // B0为超低音喇叭，0为没有超低音、1有超低音。
//                    }else {
//                        data[1] &= ~0x01;                                 // B0为超低音喇叭，0为没有超低音、1有超低音。
//                    }
//                    break;
//                case FRONT:
//                    if (spkSetup == SpkSetup.LARGE){
//                        data[1] |= 0x02;                                 // B1为前置喇叭，0为小喇叭、1为大喇叭；
//                    }else {
//                        data[1] &= ~0x02;                                 // B1为前置喇叭，0为小喇叭、1为大喇叭；
//                    }
//                    break;
//                case CENTER:
//                    data[1] &= ~0x0c;                                     // B3:2为中置喇叭，0为没有使用、1为小喇叭、2为大喇叭；
//                    if (spkSetup != SpkSetup.LARGE){
//                        data[1] |= 0x04;                                 // B3:2为中置喇叭，0为没有使用、1为小喇叭、2为大喇叭；
//                    }else if (spkSetup != SpkSetup.SMALL){
//                        data[1] |= 0x08;
//                    }
//                    break;
//                case SURROUND:
//                    data[1] &= ~0x30;                                     // B5:4为环绕声喇叭，0为没有使用、1为小喇叭、2为大喇叭；
//                    if (spkSetup != SpkSetup.LARGE){
//                        data[1] |= 0x10;
//                    }else if (spkSetup != SpkSetup.SMALL){
//                        data[1] |= 0x20;
//                    }
//                    break;
//                case BACK:
//                    data[1] &= ~0xc0;                                     // B7:6为后置喇叭，0为没有使用、1为小喇叭、2为大喇叭；
//                    if (spkSetup != SpkSetup.LARGE){
//                        data[1] |= 0x40;
//                    }else if (spkSetup != SpkSetup.SMALL){
//                        data[1] |= 0x80;
//                    }
//                    break;
//            }
//            fwbWrite(FwbUser.FWB_AUD_WR_KC3X_TYPE.Value, data);
//        }
    }
//    public void setLpfHpfFreq(boolean lpf, boolean hpf, int freq) {     // KCM_LPF_FREQ超低音通道LPF低通滤波器频率 KCM_HPF_FREQ主声道小喇叭HPF高通滤波器频率
//        byte[] data = new byte[(lpf && hpf) ? 4 : 2];
//        data[1] = (byte) (freq & 0xff);
//        if (lpf && hpf){
//            data[0] = Kc3xType.KCM_LPF_FREQ.Value;
//            data[2] = Kc3xType.KCM_HPF_FREQ.Value;
//            data[3] = data[1];
//        }else if (lpf){
//            data[0] = Kc3xType.KCM_LPF_FREQ.Value;
//        }else if (hpf){
//            data[0] = Kc3xType.KCM_HPF_FREQ.Value;
//        }
//        fwbWrite(FwbUser.FWB_AUD_WR_KC3X_TYPE.Value, data);
//    }

    public void rdEqValue(int index, boolean microphone) {                                  // 多段EQ均衡音效处理数值
    }



    private void fwbWrite(byte cmdTyp, byte[] data){
        fwbWrite(cmdTyp, data, 0, data == null ? 0 : data.length);
    }
    private void fwbWrite(byte cmdTyp, byte[] inData, int offset, int length) {
//        byte[] outData;
//        if (cmdTyp != FwbUser.FWB_CMD_NONE.Value) {
//            if (MKCDEV.mFwbProtoc == null){
//                MKCDEV.mFwbProtoc = new FwbProtoc();
//            }
//            outData = MKCDEV.mFwbProtoc.make(cmdTyp, inData, offset, length);
//        } else {
//            outData = inData;
//        }
//        if (MKCDEV.mSendFifo == null) {
//            MKCDEV.mSendFifo = new ArrayList<>();
//        }
//        MKCDEV.mSendFifo.add(outData);
//        MLOG(String.format("mFwbWrFifo.add %02x %02x", cmdTyp, outData.length));
    }


    private int getShort(int offset, byte[] inData){
        return inData[offset] | (inData[offset+1]<<8);
    }
}
