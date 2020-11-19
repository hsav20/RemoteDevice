package ltd.kcdevice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import ltd.advskin.MSKIN;
import ltd.advskin.view.KcBtnProgress;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.R;
import ltd.kcdevice.base.Kc3xType;
import ltd.kcdevice.device.DevUtils;
import main.MAPI;

import static main.MAPI.MSTRING;
import static main.MAPI.MTOAST;

public class InputSourceS1Group extends androidx.constraintlayout.widget.ConstraintLayout {
    private KcBtnProgress[] mViews;
    private int gSelect;
    private int gItemSize;
    private Integer[] gSequence;
    private String gClassName;
    private final int[] tabViewsId = new int[]{
            R.id.kbpInput1, R.id.kbpInput2, R.id.kbpInput3, R.id.kbpInput4, R.id.kbpInput5,R.id.kbpInput6, R.id.kbpInput7, R.id.kbpInput8, R.id.kbpInput9, R.id.kbpInput10,
    };
    private final int[] tabKCM_INPUT_SOURCE = new int[]{
            0x00, 0x10, 0x11, 0x12,                     // 模拟 光纤 同轴1 同轴2
            0x30, 0x31, 0x32, 0x34,                     // HDMI1 HDMI2 HDMI3 视频
            0x20,                                      // 云音乐
    };

    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    public InputSourceS1Group(Context context) {
        this(context, null);
    }
    public InputSourceS1Group(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.group_input_s1, this, true);

        gSelect = 0x1000000;                                    // 不存在的数值，用于保证初始化正常
        DevUtils devUtils = new DevUtils();
        gClassName = this.getClass().getName();
        gClassName = gClassName.substring(gClassName.lastIndexOf('.') + 1);
        gItemSize = tabKCM_INPUT_SOURCE.length;
        gSequence = devUtils.getViewSequence(gClassName, gItemSize);   // 记录VIEW使用的次序

        mViews = new KcBtnProgress[gItemSize];
        for (int index = 0; index < gItemSize; index++) {
            int sequence = getSequence(index) % gItemSize;
            // 注意使用findViewById、setTag、setViewSequence、getSequence的地方用原始的index；其余使用sequence
            mViews[sequence] = (KcBtnProgress) findViewById(tabViewsId[index]);
            mViews[sequence].setVisibility(VISIBLE);
            int value = tabKCM_INPUT_SOURCE[sequence];
            mViews[sequence].setText(MKCDEV.getRmAudCtrText(Kc3xType.KCM_INPUT_SOURCE, value));
            mViews[sequence].setTag(index);
            mViews[sequence].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int)v.getTag();
                    int sequence = getSequence(index);
                    // 注意使用findViewById、setTag、setViewSequence、getSequence的地方用原始的index；其余使用sequence
                    for (int counter = 0; counter < gItemSize; counter++){
                        if (sequence != counter){
                            mViews[counter].setChecked(false);
                        }else {
                            mViews[counter].setChecked(true);
                        }
                    }
                    DevUtils devUtils = new DevUtils();
                    devUtils.setViewSequence(gClassName, gSequence, index);          // 每次使用都调用，用于次序重新排列
                    gSelect = tabKCM_INPUT_SOURCE[sequence];
                    MKCDEV.wrKc3xType(Kc3xType.KCM_INPUT_SOURCE, gSelect);
                }
            });
        }
    }

    public void setSelect(int value){
        MLOG(String.format("KCM_INPUT_SOURCE:%02x %02x", value, gSelect));
//        MLOG(String.format("KCM_INPUT_SOURCE A :%s", MKCDEV.getRmAudCtrText(Kc3xType.KCM_INPUT_SOURCE, value)));
        if (gSelect != value) {
            for (int counter = 0; counter < gItemSize; counter++) {
                mViews[counter].setChecked(false);
            }
            for (int counter = 0; counter < gItemSize; counter++) {
                if (tabKCM_INPUT_SOURCE[counter] == value){
                    mViews[counter].setChecked(true);
                    break;
                }
            }
            gSelect = value;
        }
    }

    private int getSequence(int index){                         // 返回重新排列的次序
//MLOG(String.format("ViewSequence %02x", gSequence[index]));
        return gSequence[index] & 0xff;
    }

}