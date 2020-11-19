package ltd.kcdevice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ltd.advskin.view.KcBtnProgress;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.R;
import main.MAPI;

import static main.MAPI.MSTRING;
import static main.MAPI.MTOAST;

public class MoreS1Group extends androidx.constraintlayout.widget.ConstraintLayout {
    public KcBtnProgress kbpStandByS1, kbpFactorySetupS1, kbtBrightnessLeftS1, kbpNoiseS1, kbpDelayS1, kbpSpeakerS1, kbpNightModeDS1, kbpMoreS1;
    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    public MoreS1Group(Context context) {
        this(context, null);
    }
    public MoreS1Group(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.group_more_s1, this, true);
        kbpStandByS1 = (KcBtnProgress)findViewById(R.id.kbpStandByS1);
        kbpStandByS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MTOAST(String.format("MoreS1Group AA 待机"));
            }
        });

        kbpFactorySetupS1 = (KcBtnProgress)findViewById(R.id.kbpFactorySetupS1);
        kbpFactorySetupS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MTOAST(String.format("MoreS1Group AA 出厂设置"));
            }
        });
        kbtBrightnessLeftS1 = (KcBtnProgress)findViewById(R.id.kbtBrightnessLeftS1);
        kbtBrightnessLeftS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MTOAST(String.format("MoreS1Group AA 亮度"));
            }
        });
        kbpNoiseS1 = (KcBtnProgress)findViewById(R.id.kbpNoiseS1);
        kbpNoiseS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MKCDEV.startNoiseTestPage();
            }
        });
        kbpDelayS1 = (KcBtnProgress)findViewById(R.id.kbpDelayS1);
        kbpDelayS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MKCDEV.startNoiseTestPage();
            }
        });
        kbpSpeakerS1 = (KcBtnProgress)findViewById(R.id.kbpSpeakerS1);
        kbpSpeakerS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MKCDEV.startSpeakerSetupPage();
            }
        });
        kbpNightModeDS1 = (KcBtnProgress)findViewById(R.id.kbpNightModeDS1);
        kbpNightModeDS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MTOAST(String.format("MoreS1Group AA 夜间模式"));
            }
        });
        kbpMoreS1 = (KcBtnProgress)findViewById(R.id.kbpMoreS1);
        kbpMoreS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MTOAST(String.format("MoreS1Group AA 更多"));
            }
        });
    }
}