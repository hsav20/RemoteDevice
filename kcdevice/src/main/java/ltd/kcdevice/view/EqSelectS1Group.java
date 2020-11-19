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

import static main.MAPI.MSTRING;
import static main.MAPI.MTOAST;

public class EqSelectS1Group extends androidx.constraintlayout.widget.ConstraintLayout {
    private KcBtnProgress[] mViews;
    private int gSelect;
    private Integer[] gSequence;
    private String gClassName;
    private int gItemSize;

    private final int[] tabViewsId = new int[]{
            R.id.kbpTone1, R.id.kbpTone2, R.id.kbpTone3, R.id.kbpTone4, R.id.kbpTone5,
    };

    private final int[] tabKCM_EQ_SELECT = new int[]{
            0x00, 0x01, 0x02, 0x03, 0x04,       // 平直音效 流行音效 古典音效 摇滚音效 爵士音效
    };

    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    public EqSelectS1Group(Context context) {
        this(context, null);
    }
    public EqSelectS1Group(final Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.group_tone_s1, this, true);
        gSelect = 0x1000000;                                    // 不存在的数值，用于保证初始化正常
        DevUtils devUtils = new DevUtils();
        gClassName = this.getClass().getName();
        gClassName = gClassName.substring(gClassName.lastIndexOf('.') + 1);
        gItemSize = tabKCM_EQ_SELECT.length;
        gSequence = devUtils.getViewSequence(gClassName, gItemSize);   // 记录VIEW使用的次序
        mViews = new KcBtnProgress[gItemSize];
        for (int index = 0; index < gItemSize; index++) {
            int sequence = getSequence(index) % gItemSize;
            // 注意使用findViewById、setTag、setViewSequence、getSequence的地方用原始的index；其余使用sequence
            mViews[sequence] = (KcBtnProgress) findViewById(tabViewsId[index]);
            mViews[sequence].setVisibility(VISIBLE);
            int value = tabKCM_EQ_SELECT[sequence];
            mViews[sequence].setText(MKCDEV.getRmAudCtrText(Kc3xType.KCM_EQ_SELECT, value));
            mViews[sequence].setTag(index);
            mViews[sequence].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int)v.getTag();
                    int sequence = getSequence(index);
                    for (int counter = 0; counter < gItemSize; counter++){
                        if (index != counter){
                            mViews[counter].setChecked(false);
                        }else {
                            mViews[counter].setChecked(true);
                        }
                    }
                    DevUtils devUtils = new DevUtils();
                    devUtils.setViewSequence(gClassName, gSequence, index);          // 每次使用都调用，用于次序重新排列
                    gSelect = tabKCM_EQ_SELECT[sequence];
                    MKCDEV.wrKc3xType(Kc3xType.KCM_EQ_SELECT, gSelect);
//                    MTOAST("长按进入调节页面");
                }
            });
        }
        gSelect = 0x1000000;                                    // 不存在的数值，用于保证初始化正常
    }
    public void setSelect(int value){
        MLOG(String.format("KCM_EQ_SELECT:%02x %02x", value, gSelect));
        String text = null;
        if (gSelect != value) {
            for (int counter = 0; counter < gItemSize; counter++) {
                mViews[counter].setChecked(false);
            }
            for (int counter = 0; counter < gItemSize; counter++) {
                if (tabKCM_EQ_SELECT[counter] == value){
                    mViews[counter].setChecked(true);
                    break;
                }
            }
//            if (value == 0){
//                mSEview[4].setChecked(true);
//            }else {
//                mSEview[1 + value].setChecked(true);
//            }
            gSelect = value;
        }
    }
    private int getSequence(int index){                         // 返回重新排列的次序
        return gSequence[index] & 0xff;
    }
}