package ltd.kcdevice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import ltd.advskin.MSKIN;
import ltd.advskin.base.KcTwoListener;
import ltd.advskin.view.KcBtnProgress;
import ltd.advskin.view.KcBtnText;
import ltd.kcdevice.R;
import main.MAPI;

import static main.MAPI.MSTRING;

public class SpeakerSetupView extends androidx.constraintlayout.widget.ConstraintLayout {
    private int gIndex;
    private KcTwoListener mKcTwoListener;
    private KcBtnText kbtSpeakerSetup;
    private KcBtnProgress kbpSpeakerSetupA, kbpSpeakerSetupB, kbpSpeakerSetupC;

    private KcBtnProgress[] mSpeakerView;
    private final int[] Tab_SpeakerView = new int[]{
            R.id.kbpSpeakerSetupA, R.id.kbpSpeakerSetupB, R.id.kbpSpeakerSetupC,
    };

    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    public SpeakerSetupView(Context context) {
        this(context, null);
    }
    public SpeakerSetupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_speaker_setup, this, true);
        kbtSpeakerSetup = (KcBtnText) findViewById(R.id.kbtSpeakerSetup);
        kbpSpeakerSetupA = (KcBtnProgress) findViewById(R.id.kbpSpeakerSetupA);
        kbpSpeakerSetupB = (KcBtnProgress) findViewById(R.id.kbpSpeakerSetupB);
        kbpSpeakerSetupC = (KcBtnProgress) findViewById(R.id.kbpSpeakerSetupC);

        mSpeakerView = new KcBtnProgress[Tab_SpeakerView.length];
        for (int index = 0; index < Tab_SpeakerView.length; index++) {
            mSpeakerView[index] = (KcBtnProgress) findViewById(Tab_SpeakerView[index]);
            mSpeakerView[index].setTag(index);
            mSpeakerView[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int)v.getTag();
                    for (int counter = 0; counter < mSpeakerView.length; counter++){
                        if (index != counter){
                            mSpeakerView[counter].setChecked(false);
                        }else {
                            mSpeakerView[counter].setChecked(true);
                        }
                        if (mKcTwoListener != null){
                            mKcTwoListener.onMessage(gIndex, index);
                        }
                        MLOG(String.format("SpeakerSetupView %s", index));
                    }
                }
            });
        }

   }

    public void setTypeText(String text){
        MSKIN.setText(text, kbtSpeakerSetup);
    }
    public void setProgressA(String text){
        MSKIN.setText(text, kbpSpeakerSetupA);
    }
    public void setProgressB(String text){
        MSKIN.setText(text, kbpSpeakerSetupB);
    }
    public void setProgressC(String text){
        if (MAPI.isEmpty(text)){
            kbpSpeakerSetupC.setVisibility(GONE);
        }else {
            kbpSpeakerSetupC.setVisibility(VISIBLE);
            MSKIN.setText(text, kbpSpeakerSetupC);
        }
    }
    public void setListener(int index, KcTwoListener kcListener){
        gIndex = index;
        mKcTwoListener = kcListener;
    }

}