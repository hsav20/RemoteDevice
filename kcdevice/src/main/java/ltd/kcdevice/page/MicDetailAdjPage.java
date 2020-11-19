package ltd.kcdevice.page;

import android.media.audiofx.Equalizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ltd.advskin.C;
import ltd.advskin.MSKIN;
import ltd.advskin.base.BasePage;
import ltd.advskin.base.KcListener;
import ltd.advskin.base.KcTwoListener;
import ltd.advskin.base.KcTypeListener;
import ltd.advskin.view.KcBottomView;
import ltd.advskin.view.KcBtnImage;
import ltd.advskin.view.KcHeaderView;
import ltd.kcdevice.R;
//import ltd.kcdevice.device.BDevice;
import ltd.kcdevice.device.DevUtils;
import ltd.kcdevice.view.HorizontalAdjView;
import ltd.kcdevice.view.VerticalAdjView;
import ltd.kcdevice.view.VerticalSeekBar;
import ltd.kcdevice.view.ViewHolder;
import main.MAPI;

import static ltd.advskin.utils.AdvBitmap.C3_FG_NORMAL;
import static ltd.advskin.utils.AdvBitmap.CROP_NONE;

import static main.MAPI.MSTRING;


public class MicDetailAdjPage extends BasePage {
    private VerticalAdjView[] mEQview;
    private HorizontalAdjView[] mHorizontalAdjView;

    private String[] gsTopAStr = MSKIN.getStringArray(R.array.top_EQ_text_A);
    private String[] gsLeftStr = MSKIN.getStringArray(R.array.left_EQ_text_B);

    private final int[] Tab_EQview = new int[]{
            R.id.vvMicroEqA1, R.id.vvMicroEqA2, R.id.vvMicroEqA3, R.id.vvMicroEqA4, R.id.vvMicroEqA5,
    };
    private final int[] Tab_HorizontalAdjView = new int[]{
            R.id.havMicroEQB, R.id.havMicroEQC, R.id.havMicroEQD, R.id.havMicro1, R.id.havMicro2,
    };

    @Override
    public void onInitView(){                       // 使用者需要继承，初始化页面控件等显示
        setLayout(R.layout.page_device_micro_eq);
        mKcHeaderView = (KcHeaderView) findViewById(R.id.khvMicroEq);
        mKcHeaderView.setTitle("麦克风EQ调节", new KcTypeListener() {
            @Override
            public void onMessage(int type) {
                switch (type) {
                    case KcHeaderView.TYPE_CLICK_LEFT:
                        setClose();
                        break;
                }
            }
        });


        mEQview = new VerticalAdjView[Tab_EQview.length];
        for (int index = 0; index < Tab_EQview.length; index++) {
            mEQview[index] = (VerticalAdjView) findViewById(Tab_EQview[index]);
            mEQview[index].initView(0);
            mEQview[index].setTopText(gsTopAStr[index]);    // 顶部的字眼
            mEQview[index].setBottomText("0");  // 初始化的底部字眼
            mEQview[index].setMaxVaule(100);    // 设置移动的最大数值
            mEQview[index].setListener(index, new KcTwoListener() {
                @Override
                public void onMessage(Object object1, Object object2) {
                    if (object1 instanceof Integer && object2 instanceof Integer) {
                        if (mEQview[(int)object1] != null) {
                            mEQview[(int) object1].setBottomText(String.format("%s", object2));
                        }
                    }
                }
            });
        }

        mHorizontalAdjView = new HorizontalAdjView[Tab_HorizontalAdjView.length];
        for (int index = 0; index < Tab_HorizontalAdjView.length; index++) {
            mHorizontalAdjView[index] = (HorizontalAdjView) findViewById(Tab_HorizontalAdjView[index]);
            mHorizontalAdjView[index].setLeftText(gsLeftStr[index]);
            mHorizontalAdjView[index].setRightText("0");
            mHorizontalAdjView[index].setMaxVaule(100);
            mHorizontalAdjView[index].setListener(index, new KcTwoListener() {
                @Override
                public void onMessage(Object object1, Object object2) {
                    if (object1 instanceof Integer && object2 instanceof Integer) {
                        if (mHorizontalAdjView[(int)object1] != null) {
                            mHorizontalAdjView[(int) object1].setRightText(String.format("%s", object2));
                        }
                    }
                }
            });
        }

    }


    @Override
    public void onInitData(){                       // 初始化数据，总是在初始化控件后300毫秒被调用，实现先出来页面再出来真实数据分开，防止数据多时卡
    }
    @Override
    public void onPageUpdate(BasePage basePage){    // 更新显示页面数据，用于刷新显示 basePage返回上面的页面，可以使用getPageResult()获得返回的内容
    }
    @Override
    public void onFinish(){                         // 使用者选择性继承，用户按了后退键，不继承就直接关闭了
        setClose();
    }
}

