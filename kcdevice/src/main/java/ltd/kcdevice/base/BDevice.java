package ltd.kcdevice.base;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

public class BDevice implements Serializable {
    public byte[] InfoByte;                                 // [0-31]名字内码[32]名字长度[33]码类型 [34-39]MAC[40-43]打开日期
    public String Name;
    public String Mac;
    public int Rssi;
    public BluetoothDevice Bluetooth;

    public byte[] ProductByte;                               // 产品类型寄存器
    public byte[] RmAudCtrByte;                              // 远程音频控制寄存器
    public byte[] EqValueByte;                              // 主声道EQ[0][1][2][3]，话筒EQ[4]
}
