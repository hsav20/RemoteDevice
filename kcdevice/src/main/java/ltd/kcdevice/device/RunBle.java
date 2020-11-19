package ltd.kcdevice.device;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.provider.Settings;

import java.util.List;

import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.base.BDevice;
import ltd.kcdevice.base.FwbType;
import ltd.kcdevice.base.Kc3xType;
import ltd.kcdevice.base.NotifyListener;
import ltd.kcdevice.base.ScanListener;
import main.MAPI;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;
import static main.MAPI.MERROR;
import static main.MAPI.MSLEEP;
import static main.MAPI.MSTRING;

public class RunBle {
    public FwbProtoc mFwbProtoc;
    public boolean setClose;
    public BDevice mBDevice;
    public BDevice mSaveRegister;

    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCallback mBluetoothGattCallback;
    private int gRetry;
    private BluetoothGatt mRunGatt;
    private Object mRdWrDone;
    private boolean isRdWrDone;
    private boolean isConnection;

    private BluetoothGattCharacteristic mFwbBle;
    private BluetoothGattCharacteristic mReadBle;
    private NotifyListener mNotifyListener;
    private byte gInitRegister;									    // 初始化 产品及网络设置寄存器 音频控制寄存器 EQ数值寄存器 ,每个占用两个BIT
    private byte gRegEqDataIndex;

    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }
    public void setStart(BDevice bDevice, NotifyListener notifyListener) {
        MLOG(String.format("开始运行 %s ", bDevice.Mac));
        DevUtils devUtils = new DevUtils();
        mBDevice = bDevice;
        mSaveRegister = new BDevice();
        devUtils.copyDeviceByte(mSaveRegister, mBDevice);
        mNotifyListener = notifyListener;
        startGattCallback();
        startThread();
        startConnect();
    }
    public void initRegEqData(int index){
        gRegEqDataIndex = (byte) (0x10 | index);
    }

    private boolean setStartSpectrum(){
       // setNotification(mReadBle);                                          // 允许0xff01 Notification
        return true;
    }
    private boolean setStartFwb(){
        long tick = System.currentTimeMillis();
        while (isConnection) {
            if (setNotification(mFwbBle)){
                break;
            }
            if ((System.currentTimeMillis() - tick) > 2000){
                break;
            }
            MSLEEP(100);
        }
        MLOG(String.format("setAppPowerOn A"));
        byte[] outData = new byte[8];
        boolean isDone0 = false;
        boolean isDone1 = false;
        boolean isDone2 = false;
        tick = System.currentTimeMillis();
        while (isConnection && (System.currentTimeMillis() - tick) < 2000) {
            if (!isDone0) {
                String gsLocal_1 = Build.MODEL + "&" + Settings.Secure.getString(MAPI.mContext.getContentResolver(), "android_id");
                int checksum = MAPI.GET_SPEC_DWORD_CHECKSUM(gsLocal_1.getBytes().length, 0, gsLocal_1.getBytes());
                MAPI.DWORD_BUFF8(checksum, 0, outData);

                MAPI.DWORD_BUFF8((int) (System.currentTimeMillis() / 1000), 4, outData);
                isDone0 = fwbWrite(FwbType.FWB_AUD_WFBT_TICK, outData, 0, 8);
                MLOG(String.format("FwbType.FWB_AUD_WFBT_TICK %08x %s %s", checksum, gsLocal_1, isDone0));
                return true;
            }
            if (isDone0 && isDone1 && isDone2) {
                return true;
            }
//            MSLEEP(100);
//            if (!isDone1) {
//                outData[0] = (byte) (mBDevice.ProductByte != null ? mBDevice.ProductByte.length : 0);
//                outData[1] = 0;
//                isDone1 = fwbWrite(FwbType.FWB_AUD_PUSH_PRODUCT, outData, 0, 2);
//            }
//            MSLEEP(100);
//            if (!isDone2) {
//                outData[0] = (byte) (Kc3xType.RAC_RMAUDCTR_MAX.Value - Kc3xType.KCM_SRC_FORMAT.Value);
//                outData[1] = (byte) (Kc3xType.KCM_SRC_FORMAT.Value & 0xff);
//                isDone2 = fwbWrite(FwbType.FWB_AUD_PUSH_RMAUDCTR, outData, 0, 2);
//            }
//            initRegEqData(0);
        }
        return false;
    }
    private void mainLoop(long tick){
        gInitRegister = 0x00;                               // 已经初始化 产品及网络设置寄存器 音频控制寄存器 EQ数值寄存器
        if (!setStartFwb() || !setStartSpectrum()) {
            return;
        }
        MLOG(String.format("开始运行"));
        int flag = 0;
        while (isConnection){
            MSLEEP(500);
//            if (gInitRegister != 0x3f) {                            // 还没有初始化 寄存器
////                MLOG(String.format("还没有初始化 gInitRegister %02x",gInitRegister));
//                if ((gInitRegister & 0x03) == 0x01) {               // 已经收到 产品及网络设置寄存器
//                    if (fwbWrite((byte) (FwbType.FWB_AUD_PUSH_PRODUCT|FwbType.FWB_ACK_FLAG), null, 0, 0)) {
//                        gInitRegister |= 0x02;
//                    }
//                }
//                if ((gInitRegister & 0x0c) == 0x04) {               // 已经收到 音频控制寄存器
//                    if (fwbWrite((byte) (FwbType.FWB_AUD_PUSH_RMAUDCTR |FwbType.FWB_ACK_FLAG), null, 0, 0)) {
//                        gInitRegister |= 0x0c;
//                    }
//                }
//                if ((gInitRegister & 0x30) == 0x10) {               // 已经收到 EQ数值寄存器
//                    if (fwbWrite((byte) (FwbType.FWB_AUD_PUSH_EQ_VALUE|FwbType.FWB_ACK_FLAG), null, 0, 0)) {
//                        gInitRegister |= 0x20;
//                    }
//                }
//            }
//            if (gRegEqDataIndex != 0){
//                byte[] outData = new byte[4];
//                outData[0] = (byte)(0x20);
//                outData[1] = (byte)(gRegEqDataIndex & 0x0f);
//                boolean isDone = fwbWrite(FwbType.FWB_AUD_PUSH_EQ_VALUE, outData, 0, 2);
//                MLOG(String.format("gRegEqDataIndex %s %s", gRegEqDataIndex, isDone));
//                gRegEqDataIndex = 0;
//            }

            if (MKCDEV.mSendFifo != null && MKCDEV.mSendFifo.size() > 0){
                tick = MKCDEV.mSendFifo.get(0).Tick;
                if ((System.currentTimeMillis() - tick) < 200){
                    continue;                                   // 指令时间太短也先等等再处理
                }
                byte[][] data = new byte[MKCDEV.mSendFifo.size()][];
                for (int index = data.length; index != 0; index--){
                    data[index - 1] = MKCDEV.mSendFifo.get(0).Data;         // 反序，目的是取出来使用顺序
                    MKCDEV.mSendFifo.remove(0);
//                        MLOG(String.format("data[index - 1] %s %s %02x %s", index, data[index-1].length, data[index-1][0], data[index-1][1]));
                }
//            MLOG(String.format("需要发指令:%s(%s) 时间:%s", data.length, MKCDEV.mSendFifo.size(), System.currentTimeMillis() - tick));
                int counter;
                for (int index = 0; index < data.length; index++){
                    if (data[index][0] == FwbType.FWB_AUD_WR_RMAUDCTR) {
                        byte type = data[index][2];                      // [0]cmdTyp[1]长度[2]Kc3xType类型[3]内容
                        if (type != 0) {                                // 不为0表示有对应的Kc3xType
                            for (counter = index + 1; counter < data.length; counter++) {
                                if (data[counter][2] == type) {          // 相同类型
                                    data[counter][2] = 0;               // 去掉旧的
//                                    MLOG(String.format("counter %s", counter));
                                }
                            }
                        }
                    }
                }
                counter = 0;
                byte[] outData = new byte[FwbType.FWB_MAX_DATA];
                for (int index = 0; index < data.length; index++){
                    if (data[index][0] == FwbType.FWB_AUD_WR_RMAUDCTR) {    // [0]cmdTyp[1]长度[2]Kc3xType类型[3]内容
                        if (data[index][2] != 0) {                          // Kc3xType类型
                    MLOG(String.format("index %s %s %02x %02x %02x", index, data[index].length, data[index][1], data[index][2], data[index][3]));
                            MAPI.COPY_BUFF8(data[index].length - 1, 1, data[index], counter, outData);
//                            System.arraycopy(data[index], 1, outData, counter, data[index].length - 1);
                            counter += data[index].length - 1;              // 不传输[0]cmdTyp
                        }
                    }
                }
                tick = System.currentTimeMillis();
                if (fwbWrite(FwbType.FWB_AUD_WR_RMAUDCTR, outData, 0, counter)) {
                    MLOG(String.format("成功发送 %s字节(%sms)", counter, System.currentTimeMillis() - tick));
                }else {
                    MERROR("RunBle 发送 出错");
                }
            }
        }
    }


    private void fwbRecvDone(byte[] inData){
        MLOG(String.format("RunBle inData  %02x_%02x", inData[4], gInitRegister));
//        if (gInitRegister != 0x3f) {                    // 还没有初始化 寄存器
//            switch (inData[4]){
//                case FwbType.FWB_AUD_PUSH_PRODUCT:   // 已经初始化 产品及网络设置寄存器
//                    if ((gInitRegister&0x03) == 0) {
//                        gInitRegister |= 0x01;
//                    }
//                    break;
//                case FwbType.FWB_AUD_PUSH_RMAUDCTR:   // 已经初始化 音频控制寄存器
//                    if ((gInitRegister&0x0c) == 0) {
//                        gInitRegister |= 0x04;
//                    }
//                    break;
//                case FwbType.FWB_AUD_PUSH_EQ_VALUE:   // 已经初始化 EQ数值寄存器
//                    if ((gInitRegister&0x30) == 0) {
//                        gInitRegister |= 0x10;
//                    }
//                    break;
//            }
//        }
        if (mNotifyListener != null) {
            mNotifyListener.onMessage(inData[4], inData);
        }
        MLOG(String.format("RunBle FWB_AUD_PUSH_RMAUDCTR %02x %02x", inData[4], FwbType.FWB_AUD_PUSH_RMAUDCTR));
        switch (inData[4]){
            case FwbType.FWB_AUD_PUSH_PRODUCT:       // 已经初始化 产品及网络设置寄存器
                MLOG(String.format("FWB_AUD_PUSH_PRODUCT SaveB %02x %02x %02x ", mBDevice.ProductByte.length, mSaveRegister.ProductByte[ProductInfo.PRODUCT_KCM_MODEL], mBDevice.ProductByte[ProductInfo.PRODUCT_KCM_MODEL]));
                if (cmpAndCopy(mSaveRegister.ProductByte, mBDevice.ProductByte, mBDevice.ProductByte.length)) {
                    DevUtils devUtils = new DevUtils();
                    devUtils.saveDeviceByte(mBDevice.Mac, MKCDEV.DEVICE_BYTE_PRODUCT, mBDevice.ProductByte);
//                    MLOG(String.format("FWB_AUD_PUSH_PRODUCT SaveC %02x %02x ", mBDevice.ProductByte.length, mBDevice.ProductByte[ProductInfo.PRODUCT_KCM_MODEL]));
                }
                break;
            case FwbType.FWB_AUD_PUSH_RMAUDCTR:     // 已经初始化 音频控制寄存器
                MLOG(String.format("FWB_AUD_PUSH_RMAUDCTR SaveB %s %s", mBDevice.RmAudCtrByte, mSaveRegister.RmAudCtrByte));
                MLOG(String.format("FWB_AUD_PUSH_RMAUDCTR SaveB %02x %02x %02x ", mBDevice.RmAudCtrByte.length, mSaveRegister.RmAudCtrByte[Kc3xType.KCM_VOLUME_MAX.Value], mBDevice.RmAudCtrByte[Kc3xType.KCM_VOLUME_MAX.Value]));
                if (cmpAndCopy(mSaveRegister.RmAudCtrByte, mBDevice.RmAudCtrByte, mBDevice.RmAudCtrByte.length)) {
                    DevUtils devUtils = new DevUtils();
                    devUtils.saveDeviceByte(mBDevice.Mac, MKCDEV.DEVICE_BYTE_RMAUDCTR, mBDevice.RmAudCtrByte);
                    MLOG(String.format("FWB_AUD_PUSH_RMAUDCTR SaveC %02x %02x %02x ", mBDevice.RmAudCtrByte.length, mSaveRegister.RmAudCtrByte[Kc3xType.KCM_VOLUME_MAX.Value], mBDevice.RmAudCtrByte[Kc3xType.KCM_VOLUME_MAX.Value]));
                }
                break;
            case FwbType.FWB_AUD_PUSH_EQ_VALUE:      // 已经初始化 EQ数值寄存器
                if (cmpAndCopy(mSaveRegister.EqValueByte, mBDevice.EqValueByte, mBDevice.EqValueByte.length)) {
                    DevUtils devUtils = new DevUtils();
                    devUtils.saveDeviceByte(mBDevice.Mac, MKCDEV.DEVICE_BYTE_EQVALUE, mBDevice.EqValueByte);
                }
                break;
        }
    }


    private void scanBle(){
        MKCDEV.scanBle(new ScanListener(){
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (mBDevice.Mac.equals(device.getAddress())){
                    MKCDEV.stopScanBle(null);
                    DevUtils devUtils = new DevUtils();
                    BDevice bDevice = devUtils.addDeviceList(device.getAddress(), scanRecord, null);
                    if (bDevice != null && bDevice.InfoByte != null) {                      // bDevice有用的信息只是InfoByte
                        mBDevice.InfoByte = devUtils.objectToByte(mBDevice.InfoByte, 0x40);
                        MAPI.COPY_BUFF8(bDevice.InfoByte.length, 0, bDevice.InfoByte, 0, mBDevice.InfoByte);
                        mBDevice.Bluetooth = device;
                        mBDevice.Rssi = rssi;
//                        MLOG(String.format("startThread C %s %s %s", mBDevice.Name, gMacAddr, rssi));
                        startConnect();
                    }else {
                        MERROR("RunBle scanBle mBDevice == null");
                    }
                }else {
//                                        MLOG(String.format("RunBle Thread %s %s 扫描到其他%s", mBDevice.Name, mBDevice.Mac, device.getAddress()));
                }
            }
        });
    }

    private void startThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                setClose = false;
                long tick = System.currentTimeMillis();
                while (!setClose){
                    if (isConnection) {
                        if (mRunGatt != null) {
                            DevUtils devUtils = new DevUtils();
                            if (mFwbBle == null) {                          // 0x00ff
                                mFwbBle = devUtils.getCharacteristic(MKCDEV.GATTS_SERVICE_UUID_FWB, mRunGatt);
                            }
                            if (mReadBle == null) {                          // 0x00ee
                                mReadBle = devUtils.getCharacteristic(MKCDEV.GATTS_SERVICE_UUID_SPECTRUM, mRunGatt);
                            }
                        }
                        if (mFwbBle != null && mReadBle != null){
                            mFwbProtoc = new FwbProtoc();
                            mainLoop(System.currentTimeMillis());
                            mFwbProtoc = null;
                            tick = System.currentTimeMillis();
                        }
                    }else {
                        MSLEEP(3300);
                        MLOG(String.format("等待%s，%sms ", mBDevice.Mac, System.currentTimeMillis() - tick));
                        if ((System.currentTimeMillis() - tick) > 10000) {       // 长时间连接不到
                            tick = System.currentTimeMillis();
                            MLOG(String.format("没有找到设备%s，重新扫描 ", mBDevice.Mac));
                            scanBle();
                        }
                    }
                }
                MKCDEV.mRunBle = null;
                MKCDEV.mKcmCtrl = null;
            }
        }).start();
    }

    public boolean fwbWrite(byte cmdTyp, byte[] inData, int offset, int length){
        byte[] outData;
        if (cmdTyp != FwbType.FWB_CMD_NONE) {
            outData = mFwbProtoc.makeProtoc(cmdTyp, inData, offset, length);
        }else {
            outData = inData;
        }
        int remain = length + 8;
//        MLOG(String.format("cmdTyp:%02x outData[4]:%02x length:%02x", cmdTyp, outData[4], length));
        offset = 0;
        while (remain != 0){
            if (remain > 20){
                length = 20;
                remain -= 20;
            }else {
                length = remain;
                remain = 0;
            }
            byte[] wrData = new byte[length];
//            MLOG(String.format("length:%s remain:%s offset:%s", length, remain, offset));
            System.arraycopy(outData, offset, wrData, 0, length);
            if (!writeData(wrData, mFwbBle)){
                return false;
            }
            offset += length;
        }
        return true;
    }
    private boolean cmpAndCopy(byte[] outData, byte[] inData, int length){
        boolean isDiff = false;
        for (int counter = 0; counter < length; counter++){
            if (outData[counter] != inData[counter]){
                isDiff = true;
                outData[counter] = inData[counter];
            }
        }
        return isDiff;
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean setNotification(BluetoothGattCharacteristic characteristic) {
        boolean isEnable0 = false;
        boolean isEnable1 = false;
        try {
            isEnable0 = mRunGatt.setCharacteristicNotification(characteristic, true);
            if (isEnable0) {
                List<BluetoothGattDescriptor> descriptorList = characteristic.getDescriptors();
                if(descriptorList != null && descriptorList.size() > 0) {
//                    MLOG(String.format("%s descriptorList.size %s", characteristic.getUuid(), descriptorList.size()));
                    MSLEEP(100);                            // 未知原因，需要加大延迟！！！
                    for (BluetoothGattDescriptor descriptor : descriptorList) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        isEnable1 = mRunGatt.writeDescriptor(descriptor);
                        MSLEEP(100);                        // 未知原因，需要加大延迟！！！
//                        MLOG(String.format("%s writeDescriptor %s", characteristic.getUuid(), descriptor));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        MSLEEP(10);
//        MLOG(String.format("%s 打开接收消息 %s %s", characteristic.getUuid(), isEnable0, isEnable1));
        return (isEnable0 && isEnable1) ? true : false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean writeData(byte[] inData, BluetoothGattCharacteristic characteristic){
        for (int counter = 0; counter < 3; counter++) {
            try {
                if (characteristic.setValue(inData)) {
                    if (!mRunGatt.writeCharacteristic(characteristic)) {
                        MERROR(String.format("RunBle writeRegister C %s %02x %02x %s", mBDevice.Name, inData.length, inData[0], isConnection));
                    }
//                MLOG(String.format("%s 发送 %s字节(%02x %02x %02x)", characteristic.getUuid(), inData.length, inData[0], inData[1], inData[2]));
                }
                if (waitRdWrDone(2000)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void startGattCallback(){
        mBluetoothGattCallback = new BluetoothGattCallback() {
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                byte[] inData = characteristic.getValue();
                if (inData != null && inData.length > 0) {
                    int uuid = ((int)(characteristic.getUuid().getMostSignificantBits() >> 32));
                   MLOG(String.format("RunBle onCharacteristicChanged %04x %s", uuid, characteristic.getValue().length));
                    if (uuid == MKCDEV.GATTS_CHAR_UUID_FWB){
                        MLOG(String.format("RunBle GATTS_CHAR_UUID_FWB %02x_%02x_%02x_%02x_%02x_%02x_%02x", inData.length, inData[0], inData[1], inData[2], inData[3], inData[4], inData[5]));
                        for (byte index = 0; index < inData.length; index++) {
                            if (mFwbProtoc.recvByte(inData[index])) {          // 找到FWB协议
                                MLOG(String.format("RunBle UUID_FWB B %02x %02x %02x %02x %02x %02x", mFwbProtoc.RecvdData[0], mFwbProtoc.RecvdData[1], mFwbProtoc.RecvdData[2], mFwbProtoc.RecvdData[4], mFwbProtoc.RecvdData[6], mFwbProtoc.RecvdData[7]));
                                fwbRecvDone(mFwbProtoc.RecvdData);
                            }
                        }
                    }else if (uuid == MKCDEV.GATTS_CHAR_UUID_SPECTRUM){
//                        MLOG("READ_UUID data.length "+data.length);
                        if (mNotifyListener != null) {
//                                 MLOG(String.format("gRxFwbData[4] %02x", gRxFwbData[4]));
                            mNotifyListener.onMessage(FwbType.FWB_CMD_NONE, inData);
                        }
                    }
                }
            }
            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
            }
            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                setRdWrDone();
            }
            @Override
            public void onConnectionStateChange(final BluetoothGatt gatt, int status, final int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                //不同的手机当蓝牙关闭，设备断开（重启，远离）返回的状态不一样，newState都一样是DISCONNECTED，设备切换不会产生影响
                MLOG(String.format("status=%s newState=%s", status, newState));
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {     // 调用connect会调用
//                            connectSuccess(gatt);
                        if (gatt != null && !gatt.discoverServices()) {     // 同一个电器重复调用了
                            gatt.discoverServices();
                        }
                        MLOG(String.format("同一个电器重复调用了"));
                        connectSuccess(gatt);
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {//调用disconnect会调用，设备断开或蓝牙关闭会进入
                        connectFail(gatt);
                    }
                }else if (status == 133) {              // GATT_FAILURE = 0x101;
                    gatt.close();
                    MLOG(String.format("onConnectionStateChange gatt.close()"));
                    startConnect();
                } else {     // 调用connect和disconnect出错后会进入,设备断开或蓝牙关闭会进入
                    connectFail(gatt);
                }
            }
            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
                MLOG("onDescriptorRead");
            }
            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                MLOG(String.format("onDescriptorWrite %s %s %s", descriptor.getUuid(), status, BluetoothGatt.GATT_SUCCESS));
            }
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                super.onMtuChanged(gatt, mtu, status);
                MLOG("onMtuChanged");
            }
            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
                MLOG("onReadRemoteRssi");
            }
            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                super.onReliableWriteCompleted(gatt, status);
                MLOG("onReliableWriteCompleted");
            }
            @Override
            public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
                MLOG(String.format("onServicesDiscovered %02x", status));
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    connectSuccess(gatt);
                } else {
                    connectFail(gatt);
                }
            }
        };
    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void startConnect() {
        if (mBDevice != null) {
            BluetoothDevice device = mBDevice.Bluetooth;
            if (device != null) {
                boolean autoConnect = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    MLOG("连接设备.M " + device.getAddress());
                    mBluetoothGatt = device.connectGatt(MAPI.mContext, autoConnect, mBluetoothGattCallback, TRANSPORT_LE);
                } else {
                    MLOG("连接设备 " + device.getAddress());
                    mBluetoothGatt = device.connectGatt(MAPI.mContext, autoConnect, mBluetoothGattCallback);
                }
                return;
            }
        }
//        MERROR(String.format("RunBle->startConnect() device:%s", mBDevice));
        MLOG("没有有效的电器对象，重新扫描");
        scanBle();
    }
    private void setRdWrDone(){
        isRdWrDone = true;
        if (mRdWrDone == null){
            mRdWrDone = new Object();
        }
        synchronized (mRdWrDone) {
            mRdWrDone.notifyAll();
        }
    }
    private boolean waitRdWrDone(int timeout){
        if (mRdWrDone == null){
            mRdWrDone = new Object();
        }
        synchronized (mRdWrDone) {
            try {
                mRdWrDone.wait(timeout);                       // 等待信号
            } catch (Exception e) {
            }
        }
        if (isRdWrDone){
            isRdWrDone = false;
            return true;
        }
        return false;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void connectSuccess(BluetoothGatt gatt){
        mRunGatt = gatt;
        isConnection = true;
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void connectFail(BluetoothGatt gatt) {
        MLOG(String.format("connectFail %s %s", gatt.getDevice().getAddress(), gRetry));
//            可以不关闭，以便重用，因为在连接connect的时候可以快速连接
        DevUtils devUtils = new DevUtils();
        if (!devUtils.checkIsSamsung() || !devUtils.isBluetoothIsEnable()) {  //三星手机断开后直接连接
            if (mBluetoothGatt != null) {
                mBluetoothGatt.close();//gatt.getDevice().getAddress()); //防止出现status 133
            }
        }
        if (++gRetry < 5) {                          // 出错了在5次之内重新连接
            MLOG(String.format("连接出错，重新连接 %s", gRetry));
            startConnect();
        }
        isConnection = false;
    }

}
