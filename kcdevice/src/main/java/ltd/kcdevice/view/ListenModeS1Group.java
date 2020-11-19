package ltd.kcdevice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import ltd.advskin.view.KcBtnProgress;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.R;
import ltd.kcdevice.base.Kc3xType;
import ltd.kcdevice.device.DevUtils;
import main.MAPI;

import static ltd.advskin.MRAM.isSmall;
import static main.MAPI.MSTRING;
import static main.MAPI.MTOAST;

public class ListenModeS1Group extends androidx.constraintlayout.widget.ConstraintLayout {
    private KcBtnProgress[] mViews;
    private int gSelect;
    private Integer[] gSequence;
    private String gClassName;
    private int gItemSize;

    private final int[] tabViewsId = new int[]{
            R.id.kbpSurround1, R.id.kbpSurround2, R.id.kbpSurround3, R.id.kbpSurround4, R.id.kbpSurround5, R.id.kbpSurround6,
    };

    private final int[] tabKCM_LISTEN_MODE = new int[]{
            0x00, 0x01, 0x10, 0x20, 0x21, 0x22,     // HIFI立体声  立体声+超低音  多声道  多声道1  多声道2  多声道dsp
    };

    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }
    public ListenModeS1Group(Context context) {
        this(context, null);
    }
    public ListenModeS1Group(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.group_surround_s1, this, true);
        gSelect = 0x1000000;                                    // 不存在的数值，用于保证初始化正常
        DevUtils devUtils = new DevUtils();
        gClassName = this.getClass().getName();
        gClassName = gClassName.substring(gClassName.lastIndexOf('.') + 1);
        gItemSize = tabKCM_LISTEN_MODE.length;
        gSequence = devUtils.getViewSequence(gClassName, gItemSize);   // 记录VIEW使用的次序
        mViews = new KcBtnProgress[gItemSize];
        for (int index = 0; index < gItemSize; index++) {
            int sequence = getSequence(index) % gItemSize;
            // 注意使用findViewById、setTag、setViewSequence、getSequence的地方用原始的index；其余使用sequence
            mViews[sequence] = (KcBtnProgress) findViewById(tabViewsId[index]);
            mViews[sequence].setVisibility(VISIBLE);
            int value = tabKCM_LISTEN_MODE[sequence];
            mViews[sequence].setText(MKCDEV.getRmAudCtrText(Kc3xType.KCM_LISTEN_MODE, value));
            mViews[sequence].setTag(index);
            mViews[sequence].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int)v.getTag();
                    int sequence = getSequence(index);
                    for (int counter = 0; counter < gItemSize; counter++){
                        if (sequence != counter){
                            mViews[counter].setChecked(false);
                        }else {
                            mViews[counter].setChecked(true);
                        }
                    }
                    DevUtils devUtils = new DevUtils();
                    devUtils.setViewSequence(gClassName, gSequence, index);          // 每次使用都调用，用于次序重新排列
                    gSelect = tabKCM_LISTEN_MODE[sequence];
                    MKCDEV.wrKc3xType(Kc3xType.KCM_LISTEN_MODE, gSelect);
                }
            });
        }

        if (!isSmall) {

        } else {   // 如果检测是isSmall屏幕尺寸的时候

        }

    }

    public void setSelect(int value){
        MLOG(String.format("KCM_LISTEN_MODE:%02x %02x", value, gSelect));
        String text = null;
        if (gSelect != value) {
            for (int counter = 0; counter < gItemSize; counter++) {
                mViews[counter].setChecked(false);
            }
            for (int counter = 0; counter < gItemSize; counter++) {
                if (tabKCM_LISTEN_MODE[counter] == value){
                    mViews[counter].setChecked(true);
                    break;
                }
            }
            gSelect = value;
        }
    }

    private int getSequence(int index){                         // 返回重新排列的次序
        return gSequence[index] & 0xff;
    }

}