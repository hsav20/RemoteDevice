package ltd.kcdevice.base;

import android.bluetooth.BluetoothDevice;

public interface ScanListener {
    void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord);
}
