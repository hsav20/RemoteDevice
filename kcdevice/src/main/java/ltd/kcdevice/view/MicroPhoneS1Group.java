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
import main.MAPI;

//import static ltd.kcdevice.MKCDEV.setVolume;
import static main.MAPI.MSTRING;
import static main.MAPI.MTOAST;

public class MicroPhoneS1Group extends androidx.constraintlayout.widget.ConstraintLayout {
    public KcBtnProgress kbpMicroPhoneMore;
    private SeekBar sbMicroPhoneA, sbMicroPhoneB;
    private KcBtnText kbtMicroPhoneB, kbtMicroPhoneD;
    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    public MicroPhoneS1Group(Context context) {
        this(context, null);
    }
    public MicroPhoneS1Group(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.group_microphone_s1, this, true);
        kbtMicroPhoneB = (KcBtnText) findViewById(R.id.kbtMicroPhoneB);
        kbtMicroPhoneD = (KcBtnText) findViewById(R.id.kbtMicroPhoneD);

        kbpMicroPhoneMore = (KcBtnProgress) findViewById(R.id.kbpMicroPhoneMore);
        kbpMicroPhoneMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MKCDEV.startMicDetailAdjPage();
            }
        });

        setVolume(0);
        setVolumeB(0);
        sbMicroPhoneA = findViewById(R.id.sbMicroPhoneA);
        sbMicroPhoneA.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (sbMicroPhoneA != null) {
                        sbMicroPhoneA.setProgress(progress);
//                        MKCDEV.setVolume(progress);
                    }
                    setVolume(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sbMicroPhoneB = findViewById(R.id.sbMicroPhoneB);
        sbMicroPhoneB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (sbMicroPhoneB != null) {
                        sbMicroPhoneB.setProgress(progress);
//                        MKCDEV.setVolume(progress);
                    }
                    setVolumeB(progress);

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
    public void setVolume(int vaule){
        MSKIN.setText(String.format("%s", vaule), kbtMicroPhoneB);
    }
    public void setVolumeB(int vaule){
        MSKIN.setText(String.format("%s", vaule), kbtMicroPhoneD);
    }

}