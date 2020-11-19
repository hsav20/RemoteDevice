package ltd.kcdevice.page;

import ltd.advskin.MSKIN;
import ltd.advskin.base.BasePage;
import ltd.advskin.base.KcTwoListener;
import ltd.advskin.base.KcTypeListener;
import ltd.advskin.view.KcHeaderView;
import ltd.kcdevice.R;
import ltd.kcdevice.view.SpeakerSetupView;
import main.MAPI;
import static main.MAPI.MSTRING;

public class SpeakerSetupPage extends BasePage {
    private SpeakerSetupView[] mSpeakerSetupView;
    private String[] gsSpeakerTypeStr = MSKIN.getStringArray(R.array.speaker_type_text);
    private String[] gsProgressStrA = MSKIN.getStringArray(R.array.speaker_button_text_a);
    private String[] gsProgressStrB = MSKIN.getStringArray(R.array.speaker_button_text_b);
    private String[] gsProgressStrC = MSKIN.getStringArray(R.array.speaker_button_text_c);
    private final int[] Tab_SpeakerSetupView = new int[]{
            R.id.ssvSpeakerSetupA, R.id.ssvSpeakerSetupB, R.id.ssvSpeakerSetupC, R.id.ssvSpeakerSetupD, R.id.ssvSpeakerSetupE, R.id.ssvSpeakerSetupF, R.id.ssvSpeakerSetupG, R.id.ssvSpeakerSetupH,
    };

    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    @Override
    public void onInitView(){                       // 使用者需要继承，初始化页面控件等显示
        setLayout(R.layout.page_speaker_setup);
        mKcHeaderView = (KcHeaderView) findViewById(R.id.khvSpeakerSetup);
        mKcHeaderView.setTitle("喇叭设置", new KcTypeListener() {
            @Override
            public void onMessage(int type) {
                switch (type) {
                    case KcHeaderView.TYPE_CLICK_LEFT:
                        setClose();
                        break;
                }
            }
        });

        mSpeakerSetupView = new SpeakerSetupView[Tab_SpeakerSetupView.length];
        for (int index = 0; index < Tab_SpeakerSetupView.length; index++) {
            mSpeakerSetupView[index] = (SpeakerSetupView) findViewById(Tab_SpeakerSetupView[index]);
            mSpeakerSetupView[index].setTypeText(gsSpeakerTypeStr[index]);
            mSpeakerSetupView[index].setProgressA(gsProgressStrA[index]);
            mSpeakerSetupView[index].setProgressB(gsProgressStrB[index]);
            mSpeakerSetupView[index].setProgressC(gsProgressStrC[index]);
            mSpeakerSetupView[index].setListener(index, new KcTwoListener() {
                @Override
                public void onMessage(Object object1, Object object2) {
                    if (object1 instanceof Integer && object2 instanceof Integer) {
                        MLOG(String.format("SpeakerSetupPage AA %s_%s", object1, object2));
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

