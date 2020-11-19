package ltd.kcdevice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import ltd.advskin.MSKIN;
import ltd.advskin.view.KcBtnProgress;
import ltd.advskin.view.KcBtnText;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.R;
import ltd.kcdevice.base.Kc3xType;
import main.MAPI;

import static main.MAPI.MSTRING;

//public class VolumeS1Group extends LinearLayout {
public class VolumeS1Group extends androidx.constraintlayout.widget.ConstraintLayout {
    private KcBtnText kbtValueVolumeS1;
    private SeekBar sbAdjVolumeS1;
    private KcBtnProgress kbpMuteVolumeS1;
    private int gSetVolume;
    private int gMaxVolume;

    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    public VolumeS1Group(Context context) {
        this(context, null);
    }
    public VolumeS1Group(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.group_volume_s1, this, true);
        kbtValueVolumeS1 = findViewById(R.id.kbtValueVolumeS1);
        sbAdjVolumeS1 = findViewById(R.id.sbAdjVolumeS1);
        kbpMuteVolumeS1 = findViewById(R.id.kbpMuteVolumeS1);
        kbpMuteVolumeS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kbpMuteVolumeS1.setChecked(true);
                MLOG(String.format("VolumeS1Group AA "));
            }
        });
//        setVolume(50);
        sbAdjVolumeS1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                MLOG("fromUser A "+fromUser);
                if (fromUser) {
//                    if (sbAdjVolumeS1 != null) {
//                        sbAdjVolumeS1.setProgress(progress);
//                    }
                    MLOG("KCM_VOLUME_CTRL A "+progress);
                    setVolume(progress);
                    MKCDEV.wrKc3xType(Kc3xType.KCM_VOLUME_CTRL, progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
    public void setVolume(int value){
        MLOG(String.format("gSetVolume:%s %s", value, gSetVolume));
        if (gSetVolume != value) {
            gSetVolume = value;
            MSKIN.setText(String.format("%s", value), kbtValueVolumeS1);
            sbAdjVolumeS1.setProgress(value);
        }
    }
    public void setMaxVolume(int value){
        MLOG(String.format("gMaxVolume:%s %s", value, gMaxVolume));
        if (value < 80){
            value = 80;
        }
        if (gMaxVolume != value) {
            gMaxVolume = value;
            sbAdjVolumeS1.setMax(value);
        }
    }
}