package ltd.kcdevice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import ltd.advskin.MSKIN;
import ltd.advskin.view.KcBtnText;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.R;
import ltd.kcdevice.base.BDevice;
import ltd.kcdevice.base.Kc3xType;

public class StatusS1Group extends androidx.constraintlayout.widget.ConstraintLayout {
    private KcBtnText kbtInputStatusS1;
    private KcBtnText kbtFormatStatusS1;
    private KcBtnText kbtEqStatusS1;
    private int gInputSource;
    private int gListenMode;
    private int gEqSelect;
    private int gSrcFormat;

    public StatusS1Group(Context context) {
        this(context, null);
    }
    public StatusS1Group(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.group_status_s1, this, true);
        kbtInputStatusS1 = findViewById(R.id.kbtInputStatusS1);
        kbtFormatStatusS1 = findViewById(R.id.kbtFormatStatusS1);
        kbtEqStatusS1 = findViewById(R.id.kbtEqStatusS1);

        gInputSource = 0x1000000;                                    // 不存在的数值，用于保证初始化正常
        gListenMode = 0x1000000;                                    // 不存在的数值，用于保证初始化正常
        gEqSelect = 0x1000000;                                    // 不存在的数值，用于保证初始化正常
        gSrcFormat = 0x1000000;                                    // 不存在的数值，用于保证初始化正常

    }
    public void showInfo(BDevice bDevice){
        int value;
        String text;

        // 显示BLE WIFI 服务器 在线/不在线
        boolean isOnlineBle = MKCDEV.isOnlineBle();                                   // 查询当前是不是连接到低功耗蓝牙
        boolean isOnlineWifi = MKCDEV.isOnlineWifi();                                   // 查询当前是不是连接到Wifi内网
        boolean isOnlineSvr = MKCDEV.isOnlineSvr();                                   // 查询当前是不是通过Wifi连接到服务器

        // 显示KCM_INPUT_SOURCE 输入音源选择
        value = MKCDEV.getRmAudCtr(bDevice, Kc3xType.KCM_INPUT_SOURCE);
        if (gInputSource != value) {
            text = MKCDEV.getRmAudCtrText(Kc3xType.KCM_INPUT_SOURCE, value);
            MSKIN.setText(text, kbtInputStatusS1);
            gInputSource = value;
        }

        // 显示KCM_LISTEN_MODE 聆听模式选择
        value = MKCDEV.getRmAudCtr(bDevice, Kc3xType.KCM_LISTEN_MODE);
        if (gListenMode != value) {
            text = MKCDEV.getRmAudCtrText(Kc3xType.KCM_LISTEN_MODE, value);
//            MSKIN.setText(text, kbtInputStatusS1);
            gListenMode = value;
        }

        // 显示KCM_EQ_SELECT 多段EQ均衡音效处理选择
        value = MKCDEV.getRmAudCtr(bDevice, Kc3xType.KCM_EQ_SELECT);
        if (gEqSelect != value) {
            text = MKCDEV.getRmAudCtrText(Kc3xType.KCM_EQ_SELECT, value);
            MSKIN.setText(text, kbtEqStatusS1);
            gEqSelect = value;
        }

        // 显示KCM_SRC_FORMAT数码信号输入格式及通道信息指示，0x06
        value = MKCDEV.getRmAudCtr(bDevice, Kc3xType.KCM_SRC_FORMAT);
        if (gSrcFormat != value) {
            text = MKCDEV.getRmAudCtrText(Kc3xType.KCM_SRC_FORMAT, value);
            MSKIN.setText(text, kbtFormatStatusS1);
            gSrcFormat = value;
        }
    }

}
