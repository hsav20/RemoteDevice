package ltd.kcdevice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import ltd.advskin.MSKIN;
import ltd.advskin.view.KcBtnProgress;
import ltd.advskin.view.KcBtnText;
import ltd.kcdevice.R;
import main.MAPI;

import static main.MAPI.MSTRING;
import static main.MAPI.MTOAST;

public class PlayS1Group extends androidx.constraintlayout.widget.ConstraintLayout {
    private KcBtnText kbtPlay8, kbtPlay9;
    private SeekBar sbPlay;
    private KcBtnProgress[] mPlayview;
    private String[] gsPlayTxet = MSKIN.getStringArray(R.array.play_txet);
    private final int[] Tab_Playview = new int[]{
            R.id.kbpPlay1, R.id.kbpPlay2, R.id.kbpPlay3, R.id.kbpPlay4, R.id.kbpPlay5, R.id.kbpPlay6, R.id.kbpPlay7, R.id.kbpPlay8,
    };
    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    public PlayS1Group(Context context) {
        this(context, null);
    }
    public PlayS1Group(final Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.group_play_s1, this, true);
        kbtPlay8 = (KcBtnText) findViewById(R.id.kbtPlay8);
        kbtPlay9 = (KcBtnText) findViewById(R.id.kbtPlay9);
        sbPlay = (SeekBar) findViewById(R.id.sbPlay);
        sbPlay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (sbPlay != null) {
                        sbPlay.setProgress(progress);
                    }
//                    setTime(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        mPlayview = new KcBtnProgress[Tab_Playview.length];
        for (int index = 0; index < Tab_Playview.length; index++) {
            mPlayview[index] = (KcBtnProgress) findViewById(Tab_Playview[index]);
            mPlayview[index].setText(gsPlayTxet[index]);
            mPlayview[index].setTag(index);
            mPlayview[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int)v.getTag();
                    for (int counter = 0; counter < mPlayview.length; counter++){

                    }
                    MLOG(String.format("PlayS1Group %s", index));
                }
            });
        }
    }

    public void setTime(int vaule){
        MSKIN.setText(String.format("%s", vaule), kbtPlay8);
    }
}