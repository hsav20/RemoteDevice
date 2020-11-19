package ltd.kcdevice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import ltd.advskin.MSKIN;
import ltd.advskin.base.KcListener;
import ltd.advskin.base.KcTwoListener;
import ltd.advskin.view.KcBtnText;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.R;
import main.MAPI;

import static main.MAPI.MSTRING;

public class VerticalAdjView extends androidx.constraintlayout.widget.ConstraintLayout {
    private KcBtnText kbtVerticalAdjTop, kbtVerticalAdjBottom;
    private VerticalSeekBar vsbVerticalAdjBar;
    private KcTwoListener mKcTwoListener;
    private int gIndex;

    public VerticalAdjView(Context context) {
        this(context, null);
    }
    public VerticalAdjView(Context context, AttributeSet attrs) {
        super(context, attrs);


   }
    public void initView(int style){
        Context context = MAPI.mContext;
        if (style == 0) {
            LayoutInflater.from(context).inflate(R.layout.item_vertical_adj, this, true);
            kbtVerticalAdjTop = (KcBtnText) findViewById(R.id.kbtVerticalAdjTop);
            kbtVerticalAdjBottom = (KcBtnText) findViewById(R.id.kbtVerticalAdjBottom);
            vsbVerticalAdjBar = (VerticalSeekBar) findViewById(R.id.vsbVerticalAdjBar);
        }else {
            LayoutInflater.from(context).inflate(R.layout.item_vertical_adj_noise, this, true);
            kbtVerticalAdjTop = (KcBtnText) findViewById(R.id.kbtVerticalNoiseTop);
            kbtVerticalAdjTop.setVisibility(GONE);
            kbtVerticalAdjBottom = (KcBtnText) findViewById(R.id.kbtVerticalNoiseBottom);
            vsbVerticalAdjBar = (VerticalSeekBar) findViewById(R.id.vsbVerticalNoiseBar);
        }
        vsbVerticalAdjBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    if (mKcTwoListener != null){
                        mKcTwoListener.onMessage(gIndex, progress);
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

    public void setTopText(String top){
        MSKIN.setText(top, kbtVerticalAdjTop);
    }
    public void setBottomText(String text){
        MSKIN.setText(text, kbtVerticalAdjBottom);
    }
    public void setMaxVaule(int vaule){
//        MSKIN.setText(String.format("%s", vaule), kbtVerticalAdjBottom);
    }
    public void setProgress(int progress){
        MSKIN.setText(String.format("%s", progress), kbtVerticalAdjBottom);
    }
    public void setListener(int index, KcTwoListener kcListener){
        gIndex = index;
        mKcTwoListener = kcListener;
    }
}