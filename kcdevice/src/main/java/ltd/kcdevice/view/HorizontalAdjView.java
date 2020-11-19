package ltd.kcdevice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.SeekBar;

import ltd.advskin.MSKIN;
import ltd.advskin.base.KcTwoListener;
import ltd.advskin.view.KcBtnText;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.R;

import static main.MAPI.MSTRING;

public class HorizontalAdjView extends androidx.constraintlayout.widget.ConstraintLayout {
    private KcBtnText kbtHorizontalLeft, kbtHorizontalRight;
    private SeekBar sbHorizontal;
    private KcTwoListener mKcTwoListener;
    private int gIndex;

    public HorizontalAdjView(Context context) {
        this(context, null);
    }
    public HorizontalAdjView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_horizontal_adj, this, true);
        kbtHorizontalLeft = (KcBtnText) findViewById(R.id.kbtHorizontalLeft);
        kbtHorizontalRight = (KcBtnText) findViewById(R.id.kbtHorizontalRight);
        sbHorizontal = (SeekBar) findViewById(R.id.sbHorizontal);
        sbHorizontal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (mKcTwoListener != null){
                        mKcTwoListener.onMessage(gIndex, progress);
                        MSTRING(String.format("HorizontalAdjView BB %s_%s", gIndex, progress));
                    }
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

    public void setLeftText(String left){
        MSKIN.setText(left, kbtHorizontalLeft);
    }
    public void setRightText(String text){
        MSKIN.setText(text, kbtHorizontalRight);
    }
    public void setMaxVaule(int vaule){
//        MSKIN.setText(String.format("%s", vaule), kbtHorizontalRight);
    }
    public void setProgress(int progress){
        MSKIN.setText(String.format("%s", progress), kbtHorizontalRight);
    }
    public void setListener(int index, KcTwoListener kcListener){
        gIndex = index;
        mKcTwoListener = kcListener;
    }
}