package ltd.kcdevice.device;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.pm.PackageManager;
import android.os.Build;

import com.kcbsdk.MyManage.APhotoInfo;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import ltd.advskin.MSKIN;
import ltd.advskin.VARIA;
import ltd.advskin.base.KcListener;
import ltd.advskin.permission.PermissionsChecker;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.base.BDevice;
import ltd.kcdevice.base.FwbType;
import ltd.kcdevice.base.Kc3xType;
import ltd.kcdevice.base.ScanListener;
import main.MAPI;

import static main.MAPI.MSTRING;
import static main.MAPI.MTOAST;
import static main.MAPI.mActivity;


public class DevUtils {
    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    public List<BDevice> getBDeviceList() {
        List<Object> lists = (List) MSKIN.getDbsList(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_INFO_BYTE);         // getDbsList顺序显示
        int length = lists.size();
        List<BDevice> devices = new ArrayList<>();
        for (int index = 0; index < length; index++) {
            BDevice bDevice = infoByteToBDevice(lists.get(index));
            if (bDevice != null){
                devices.add(bDevice);
            }
        }
        return devices;
    }
    public BDevice getBDevice(String mac) {
        Object object = MSKIN.getDbsItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_INFO_BYTE, mac);
        if (object != null) {
            return infoByteToBDevice(object);
        }
        BDevice bDevice = new BDevice();
        bDevice.Mac = mac;
        return bDevice;
    }
    public boolean getDevice(String mac) {      // 判断设备是否已经加入列表
        if (MSKIN.getDbsItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_INFO_BYTE, mac) != null){
            return true;
        }
        return false;

    }
    public void saveDevice(BDevice bDevice) {      // 将设备加入到列表中
        MSKIN.setDbsItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_INFO_BYTE, bDevice.Mac, bDevice.InfoByte);
//        MSKIN.setDbsItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_SETUP, bDevice.Mac, bDevice.SetupByte);
    }


    public void deleteDevice(BDevice bDevice) {      // 将设备从列表中删除
        MSKIN.delDbsItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_INFO_BYTE, bDevice.Mac);
        MSKIN.delDbsItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_BYTE_PRODUCT, bDevice.Mac);
        MSKIN.delDbsItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_BYTE_RMAUDCTR, bDevice.Mac);
        MSKIN.delDbsItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_BYTE_EQVALUE, bDevice.Mac);
        MSKIN.delDbsItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_TIME, bDevice.Mac);
    }

    public void deleteAllDevice() {      // 删除全部设备
        MSKIN.delDbsAllItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_INFO_BYTE);
        MSKIN.delDbsAllItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_BYTE_PRODUCT);
        MSKIN.delDbsAllItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_BYTE_RMAUDCTR);
        MSKIN.delDbsAllItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_BYTE_EQVALUE);
        MSKIN.delDbsAllItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_TIME);
    }

    public void timeDevice(BDevice bDevice){
        long time = System.currentTimeMillis();
        MSKIN.setDbsItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_TIME, bDevice.Mac, time);
    }


    public void saveDeviceByte(String mac, int DEVICE_BYTE, byte[] inData){
        MLOG(String.format("saveDeviceByte %02x %02x", DEVICE_BYTE, inData.length));
        MSKIN.setDbsItem(VARIA.DBS_DEVICE_INFO, DEVICE_BYTE, mac, inData);
    }
    public byte[] objectToByte(Object object, int maxSize){
        if (object instanceof byte[]) {
            return (byte[])object;
        }
        return new byte[maxSize];
    }

    public void copyDeviceByte(BDevice trg, BDevice src){
        if (trg != null && src != null) {
            if (src.ProductByte != null) {
                trg.ProductByte = objectToByte(trg.ProductByte, Kc3xType.RAC_PRODUCT_MAX.Value);
                MAPI.COPY_BUFF8(src.ProductByte.length, 0, src.ProductByte, 0, trg.ProductByte);
            }

            if (src.RmAudCtrByte != null) {
                trg.RmAudCtrByte = objectToByte(trg.RmAudCtrByte, Kc3xType.RAC_RMAUDCTR_MAX.Value);
                MAPI.COPY_BUFF8(src.RmAudCtrByte.length, 0, src.RmAudCtrByte, 0, trg.RmAudCtrByte);
            }

            if (src.EqValueByte != null) {
                trg.EqValueByte = objectToByte(trg.EqValueByte, Kc3xType.RAC_EQVALUE_INDEX_MAX.Value * Kc3xType.RAC_EQVALUE_DATA_MAX.Value);
                MAPI.COPY_BUFF8(src.EqValueByte.length, 0, src.EqValueByte, 0, trg.EqValueByte);
            }
        }
    }
    public void setSelect(String mac) {
        MSKIN.setDbsItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_SELECT, "SELECT", mac);

    }
    public String getSelect() {
        Object object = MSKIN.getDbsItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_SELECT, "SELECT");
        if (object instanceof String){
            return (String) object;
        }
        return null;
    }
    public void setViewSequence(String className, Integer[] inData, int index) {
        if (!MAPI.isEmpty(className) && inData != null) {
            if (index < inData.length) {                 // [1]]用于原始使用的次数
                inData[index] += 0x100;
//                MLOG(String.format("setViewSequence %s %s %02x", className, index, inData[index]));
                MSKIN.setDbsItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_VIEW_SEQUENCE, className, inData);
            }
        }
    }
    public Integer[] getViewSequence(String className, int length) {
        Integer[] outData;
        if (!MAPI.isEmpty(className)) {
            Object object = MSKIN.getDbsItem(VARIA.DBS_DEVICE_INFO, MKCDEV.DEVICE_VIEW_SEQUENCE, className);
            if (object instanceof Integer[]) {
                outData = (Integer[])object;
                if (outData.length == length) {
                    List<Integer> lists = new ArrayList<Integer>(Arrays.asList(outData));
                    Collections.sort(lists, new Comparator<Integer>() {
                        public int compare(Integer info1, Integer info2) {
                            return info2 - info1;                         // 降序 (info1-info2为升序)
                        }
                    });
//                MLOG(String.format("getViewSequence A %s %s %s", className, outData.length, lists.size()));
//                MLOG(String.format("getViewSequence B %02x %02x %02x", lists.get(0), lists.get(1), lists.get(2)));
                    return lists.toArray(new Integer[outData.length]);
                }
            }
        }
        outData = new Integer[length];                                  // B31-B8用于原始使用的次数 B7-B0用于索引
        for (int counter = 0; counter < length; counter++) {
            outData[counter] = ((length-counter) << 8) | counter;       // 原始使用的次数是倒序的
        }
//        MLOG(String.format("getViewSequence INIT %s %s %s", className, length, outData.length));
        return outData;
    }


    private BDevice infoByteToBDevice(Object object) {
        if (object instanceof byte[]) {
            byte[] info = (byte[]) object;
            if (info.length >= 40) {                                       // [0-31]名字内码[32]名字长度[33]码类型 [34-39]MAC
                BDevice bDevice = new BDevice();
                bDevice.InfoByte = new byte[info.length];
                System.arraycopy(info, 0, bDevice.InfoByte, 0, info.length);
                bDevice.Mac = String.format("%02X:%02X:%02X:%02X:%02X:%02X", info[34 + 0], info[34 + 1], info[34 + 2], info[34 + 3], info[34 + 4], info[34 + 5]);
//MLOG(String.format("bDevice.Mac B %s", MAPI.HEX_STRING_ASCII(6, 34, bDevice.InfoByte))) ;
                bDevice.Name = byteToString(info, 0, info[32], info[33]);
                bDevice.ProductByte = loadDeviceByte(bDevice.Mac,  MKCDEV.DEVICE_BYTE_PRODUCT, Kc3xType.RAC_PRODUCT_MAX.Value);
MLOG(String.format("FWB_AUD_PUSH_PRODUCT load %02x %02x", bDevice.ProductByte.length, bDevice.ProductByte[ProductInfo.PRODUCT_KCM_MODEL])) ;

                bDevice.RmAudCtrByte = loadDeviceByte(bDevice.Mac, MKCDEV.DEVICE_BYTE_RMAUDCTR, Kc3xType.RAC_RMAUDCTR_MAX.Value);
MLOG(String.format("FWB_AUD_PUSH_RMAUDCTR load %02x %02x", bDevice.RmAudCtrByte.length, bDevice.RmAudCtrByte[Kc3xType.KCM_VOLUME_MAX.Value])) ;

                bDevice.EqValueByte = loadDeviceByte(bDevice.Mac, MKCDEV.DEVICE_BYTE_EQVALUE, Kc3xType.RAC_EQVALUE_INDEX_MAX.Value * Kc3xType.RAC_EQVALUE_DATA_MAX.Value);
                return bDevice;
            }
        }
        return null;
    }
    private byte[] loadDeviceByte(String mac, int DEVICE_BYTE, int maxSize){
        Object object = MSKIN.getDbsItem(VARIA.DBS_DEVICE_INFO,  DEVICE_BYTE, mac);
        return objectToByte(object, maxSize);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean checkIsSamsung() {                            // 判断手机类型是否是三星
        String brand = Build.BRAND;
        if (brand.toLowerCase().equals("samsung")) {
            return true;
        }
        return false;
    }
    public boolean isBluetoothIsEnable() {
        if (BluetoothAdapter.getDefaultAdapter() == null || !mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }



    public BDevice addDeviceList(String mac, byte[]scanRecord, List<BDevice> devices) {
        int length = (devices != null) ? devices.size() : 0;
        MLOG(String.format("DevUtils addDeviceList AA %s", length));
        for (int index = 0; index < length; index++) {
            MLOG(String.format("DevUtils addDeviceList BB (%s)_(%s)_%s", mac, devices.get(index).Mac, mac.equals(devices.get(index).Mac)));
            if (mac.equals(devices.get(index).Mac)) {      // 列表已经有了
                return devices.get(index);
            }
        }
        BDevice bDevice = new BDevice();
        bDevice.InfoByte = new byte[40];
        bDevice.InfoByte[32] = getNameByte(scanRecord, 0, bDevice.InfoByte);
        bDevice.InfoByte[33] = 3;                       // CODE_GB2312 = 0x03,										// 字符集为GB2312（GBK）
        String macOnly = mac.replace(":", "");      // 去掉所有 :
        MAPI.STRING_ASCII_HEX(macOnly, 6, 34, bDevice.InfoByte);
//MLOG(String.format("addDeviceList.Mac A %s", mac)) ;
//MLOG(String.format("addDeviceList.Mac B %s", MAPI.HEX_STRING_ASCII(6, 34, bDevice.InfoByte))) ;
        bDevice.Mac = mac;
        MLOG(String.format("DevUtils addDeviceList CC %s", bDevice.InfoByte[0]));
        if (bDevice.InfoByte[0] != 0) {
            bDevice.Name = byteToString(bDevice.InfoByte, 0, bDevice.InfoByte[32], bDevice.InfoByte[33]);
        }
        MLOG(String.format("DevUtils addDeviceList DD %s", devices));
        if (devices != null) {
            devices.add(bDevice);
            MLOG(String.format("DevUtils addDeviceList EE %s", devices.size()));
        }
        return bDevice;
    }


    //typedef enum {
//    CODE_NONE = 0x00,										// 不支持的字符集
//    CODE_UNICODE = 0x01,									// 字符集为UNICODE
//    CODE_UTF_8 = 0x02,										// 字符集为UTF-8
//    CODE_GB2312 = 0x03,										// 字符集为GB2312（GBK）
//    CODE_BIG5 = 0x04,										// 字符集为BIG5
//} TEXT_CODE;
    public String byteToString(byte[] infoByte, int offset, byte length, byte charCode) {
        byte[] nameByte = new byte[length];
        System.arraycopy(infoByte, offset, nameByte, 0, length);
        String strCode = (charCode == FwbType.CODE_GB2312) ? "gbk" : "utf-8";
        try {
            return new String(nameByte, strCode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
// [0-1]调试端口[2]内网[3]主机网卡[4-9]主机MAC[10-13]主机IP[14-19]从机MAC[20-23]从机IP[24-27]掩码[28-31]网关[32-35]DNS[36-39]WIFI固件日期[40]WIFI芯片[41]DHCP[42]发射WIFI
// [43-60]保留[61]产品类型[62]产品子类[63]域名简称[64-72]域名[73-81]解析密码[82U]牌子[83U]型号[84U]昵称[85A]发射WIFI名[86A]当前WIFI

    public byte getNameByte(byte[] scanRecord, int offset, byte[] outName) {
        ByteBuffer buffer = ByteBuffer.wrap(scanRecord).order(ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length != 0) {
                byte type = buffer.get();
                if (type == 0x09) {
                    buffer.get(outName, offset, length-1);
                    return (byte)(length - 1);
//                    byte[] temp = new byte[length - 1];
//                    buffer.get(temp);
////                    System.arraycopy(temp, 0, outName, offset, temp.length);
//                    for (int index = 0; index < temp.length; index++){
//                        outName[offset + index] = temp[index];
//                    }
//                    return (byte)(temp.length);
                }else {
                    buffer.position(buffer.position() + length - 1);
                }
            }else {
                break;
            }
        }
        return 0;
    }
    public void addUuids(byte[] scanRecord, List<UUID> uuids) {
        ByteBuffer buffer = ByteBuffer.wrap(scanRecord).order(ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length != 0){
                byte type = buffer.get();
                switch (type) {
                    case 0x02: // Partial list of 16-bit UUIDs
                    case 0x03: // Complete list of 16-bit UUIDs
                        while (length >= 2) {
                            uuids.add(UUID.fromString(String.format(
                                    "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
                            length -= 2;
                        }
                        break;
                    case 0x06: // Partial list of 128-bit UUIDs
                    case 0x07: // Complete list of 128-bit UUIDs
                        while (length >= 16) {
                            long lsb = buffer.getLong();
                            long msb = buffer.getLong();
                            uuids.add(new UUID(msb, lsb));
                            length -= 16;
                        }
                        break;
                    default:
                        buffer.position(buffer.position() + length - 1);
                        break;
                }
            }else {
                break;
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BluetoothGattCharacteristic getCharacteristic(int need, BluetoothGatt gatt){
        List<BluetoothGattService> bluetoothGattServices = gatt.getServices();
        for (int index = 0; index < bluetoothGattServices.size(); index++) {
            UUID uuid = bluetoothGattServices.get(index).getUuid();
            if (((int)(uuid.getMostSignificantBits() >> 32)) == need){
                List<BluetoothGattCharacteristic> list = bluetoothGattServices.get(index).getCharacteristics();
                if (list != null && list.size() > 0) {
                    return list.get(0);
                }
            }
//            MLOG(String.format("getControlIndex C %s %02x %02x", uuid, uuid.getMostSignificantBits(), (int)(uuid.getMostSignificantBits() >> 32)));
        }
        return null;
    }

    public String getName(byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<UUID>();
        String name = null;
        if( advertisedData != null ) {
            ByteBuffer buffer = ByteBuffer.wrap(advertisedData).order(ByteOrder.LITTLE_ENDIAN);
            while (buffer.remaining() > 2) {
                byte length = buffer.get();
                if (length == 0) break;

                byte type = buffer.get();
                switch (type) {
                    case 0x02: // Partial list of 16-bit UUIDs
                    case 0x03: // Complete list of 16-bit UUIDs
                        while (length >= 2) {
                            uuids.add(UUID.fromString(String.format(
                                    "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
                            length -= 2;
                        }
                        break;
                    case 0x06: // Partial list of 128-bit UUIDs
                    case 0x07: // Complete list of 128-bit UUIDs
                        while (length >= 16) {
                            long lsb = buffer.getLong();
                            long msb = buffer.getLong();
                            uuids.add(new UUID(msb, lsb));
                            length -= 16;
                        }
                        break;
                    case 0x09:
                        byte[] nameBytes = new byte[length - 1];
                        buffer.get(nameBytes);
                        try {
                            name = new String(nameBytes, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        buffer.position(buffer.position() + length - 1);
                        break;
                }
            }
            return name;
        }
        return null;
    }
    public void searchBle(ScanListener scanListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            MTOAST("当前手机系统版本太低，暂时只是提示");
            return;
        }
        if (isBluetoothIsEnable()) {           // 打开了蓝牙
            PermissionsChecker checker = new PermissionsChecker();
            String[] PERMISSIONS = checker.getFineLocation();
            if (!checker.lacksPermissions(PERMISSIONS)) {         // 已经有权限
                searchBle(false, scanListener);
            }else {                                             // 还没有权限
                MKCDEV.mScanListener = scanListener;
                MSKIN.startPermissionsActivity(PERMISSIONS, new KcListener() {
                    @Override
                    public void onMessage(Object object) {
                        DevUtils devUtils = new DevUtils();
                        if (object == null){                    // 用户打开了权限
                            devUtils.searchBle(false, MKCDEV.mScanListener);
                        }else {
                            MTOAST("没有蓝牙权限，暂时只是提示");
                        }
                    }
                });
            }
        }else {                                         // 没有打开蓝牙，暂时只是提示
            MTOAST("没有打开蓝牙，暂时只是提示");
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void searchBle(boolean stop, ScanListener scanListener) {
        if (stop && MKCDEV.mBluetooth == null && MKCDEV.mLeScanCallback == null) {    // 关闭时，如果全局变量对象不存在就不需要做了
            return;
        }
        MKCDEV.isSetStop = stop;
        MKCDEV.mScanListener = scanListener;
        if (MKCDEV.mBluetooth == null) {
            MKCDEV.mBluetooth = BluetoothAdapter.getDefaultAdapter();
        }
        if (MKCDEV.mLeScanCallback == null){
            MKCDEV.mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (MKCDEV.mScanListener != null) {
                        MKCDEV.mScanListener.onLeScan(device, rssi, scanRecord);
                    }
                    if (!MKCDEV.isSetStop) {
                        if ((System.currentTimeMillis() - MKCDEV.gStopTick) > 5000){
                            MKCDEV.stopScanBle(null);
                        }
                    }else {
                        MKCDEV.mBluetooth = null;
                        MKCDEV.mLeScanCallback = null;
                    }
                }
            };
        }
        if (!stop) {
            MKCDEV.gStopTick = System.currentTimeMillis();
            MKCDEV.mBluetooth.startLeScan(null, MKCDEV.mLeScanCallback);
        }else {
            MKCDEV.mBluetooth.stopLeScan(MKCDEV.mLeScanCallback);
        }
    }

//    从ESP32 BLE应用理解GATT_zhejfl的博客-CSDN博客
//    https://blog.csdn.net/zhejfl/article/details/85136102//    低功耗蓝牙BLE对应Gatt的UUID_司马懿的西山居-CSDN博客
//    https://blog.csdn.net/chy555chy/article/details/52229670
// #define GAP_SERVICE_UUID             0x00001800 // Generic Access Profile 通用接入规范
// #define GATT_SERVICE_UUID            0x00001801 // Generic Attribute Profile 通用属性规范
// notify和indication的区别在于，notify只是将你要发的数据发送给手机，没有确认机制，不会保证数据发送是否到达。
// 而indication的方式在手机收到数据时会主动回一个ack回来。即有确认机制，只有收到这个ack你才能继续发送下一个数据。
// 这保证了数据的正确到达，也起到了流控的作用。所以在打开通知的时候，需要设置一下。
}

//            if (lists.get(index) instanceof byte[]) {
//                byte[] info = (byte[]) lists.get(index);
//                if (info.length >= 40) {                                       // [0-31]名字内码[32]名字长度[33]码类型 [34-39]MAC
//                    BDevice bDevice = new BDevice();
//                    bDevice.InfoByte = new byte[info.length];
//                    System.arraycopy(info, 0, bDevice.InfoByte, 0, info.length);
//                    bDevice.Mac = String.format("%02X:%02X:%02X:%02X:%02X:%02X", info[34 + 0], info[34 + 1], info[34 + 2], info[34 + 3], info[34 + 4], info[34 + 5]);
////MLOG(String.format("bDevice.Mac B %s", MAPI.HEX_STRING_ASCII(6, 34, bDevice.InfoByte))) ;
//
//                    bDevice.Name = byteToString(info, 0, info[32], info[33]);
//                    devices.add(bDevice);
//                }
//            }
