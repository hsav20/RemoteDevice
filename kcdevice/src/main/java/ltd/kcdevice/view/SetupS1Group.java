package ltd.kcdevice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ltd.advskin.view.KcBtnProgress;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.R;
import main.MAPI;

import static main.MAPI.MSTRING;
import static main.MAPI.MTOAST;

public class SetupS1Group extends androidx.constraintlayout.widget.ConstraintLayout {
    public KcBtnProgress kbpSetupS1;
    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    public SetupS1Group(Context context) {
        this(context, null);
    }
    public SetupS1Group(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.group_setup_s1, this, true);
        kbpSetupS1 = (KcBtnProgress) findViewById(R.id.kbpSetupS1);
        kbpSetupS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MKCDEV.startSetupPage();
            }
        });
    }

}