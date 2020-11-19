package ltd.kcdevice.page;

import ltd.advskin.MSKIN;
import ltd.advskin.base.BasePage;
import ltd.advskin.base.KcTwoListener;
import ltd.advskin.base.KcTypeListener;
import ltd.advskin.view.KcBtnText;
import ltd.advskin.view.KcHeaderView;
import ltd.kcdevice.R;
import ltd.kcdevice.view.HorizontalAdjView;
import ltd.kcdevice.view.VerticalAdjView;
import main.MAPI;

//import ltd.kcdevice.device.BDevice;


public class NoiseTestPage extends BasePage {
    private KcBtnText[] mKcBtnTextview;
    private String[] gsNoiseTypeStr = MSKIN.getStringArray(R.array.noise_type_text);
    private final int[] Tab_NoiseTypeView = new int[]{
            R.id.kbtNoiseTestA, R.id.kbtNoiseTestB, R.id.kbtNoiseTestC, R.id.kbtNoiseTestD, R.id.kbtNoiseTestE, R.id.kbtNoiseTestF, R.id.kbtNoiseTestG, R.id.kbtNoiseTestH,
    };

    private VerticalAdjView[] mNoiseView;
    private final int[] Tab_NoiseView = new int[]{
            R.id.havNoiseTestA, R.id.havNoiseTestB, R.id.havNoiseTestC, R.id.havNoiseTestD, R.id.havNoiseTestE, R.id.havNoiseTestF, R.id.havNoiseTestG, R.id.havNoiseTestH,
    };

    private HorizontalAdjView havNoiseTestI;

    @Override
    public void onInitView(){                       // 使用者需要继承，初始化页面控件等显示
        setLayout(R.layout.page_noise_test);
        mKcHeaderView = (KcHeaderView) findViewById(R.id.khvNoiseTest);
        mKcHeaderView.setTitle("噪音测试", new KcTypeListener() {
            @Override
            public void onMessage(int type) {
                switch (type) {
                    case KcHeaderView.TYPE_CLICK_LEFT:
                        setClose();
                        break;
                }
            }
        });

        havNoiseTestI = (HorizontalAdjView) findViewById(R.id.havNoiseTestI);
        havNoiseTestI.setLeftText("0");
        havNoiseTestI.setRightText("0");
        havNoiseTestI.setMaxVaule(100);
        havNoiseTestI.setListener(0, new KcTwoListener() {
                @Override
                public void onMessage(Object object1, Object object2) {
                    if (object1 instanceof Integer && object2 instanceof Integer) {
                        if (havNoiseTestI != null) {
                            havNoiseTestI.setRightText(String.format("%s", object2));
                        }
                    }
                }
            });




        mKcBtnTextview = new KcBtnText[Tab_NoiseTypeView.length];
        for (int index = 0; index < Tab_NoiseTypeView.length; index++) {
            mKcBtnTextview[index] = (KcBtnText) findViewById(Tab_NoiseTypeView[index]);
            MSKIN.setText(gsNoiseTypeStr[index],mKcBtnTextview[index]);
        }


        mNoiseView = new VerticalAdjView[Tab_NoiseView.length];
        for (int index = 0; index < Tab_NoiseView.length; index++) {
            mNoiseView[index] = (VerticalAdjView) findViewById(Tab_NoiseView[index]);
            mNoiseView[index].initView(1);
            mNoiseView[index].setBottomText("0");  // 初始化的底部字眼
            mNoiseView[index].setMaxVaule(100);    // 设置移动的最大数值
            mNoiseView[index].setMaxVaule(100);
            mNoiseView[index].setListener(index, new KcTwoListener() {
                @Override
                public void onMessage(Object object1, Object object2) {
                    if (object1 instanceof Integer && object2 instanceof Integer) {
                        if (mNoiseView[(int)object1] != null) {
                            mNoiseView[(int) object1].setBottomText(String.format("%s", object2));
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

