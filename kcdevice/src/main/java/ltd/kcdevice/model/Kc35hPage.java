package ltd.kcdevice.model;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import ltd.advskin.base.BasePage;
import ltd.advskin.base.KcTypeListener;
import ltd.advskin.view.KcHeaderView;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.R;
import ltd.kcdevice.base.BDevice;
import ltd.kcdevice.base.Kc3xType;
import ltd.kcdevice.device.ProductInfo;
import ltd.kcdevice.view.InputSourceS1Group;
import ltd.kcdevice.view.MicroPhoneS1Group;
import ltd.kcdevice.view.MoreS1Group;
import ltd.kcdevice.view.PlayS1Group;
import ltd.kcdevice.view.SetupS1Group;
import ltd.kcdevice.view.StatusS1Group;
import ltd.kcdevice.view.ListenModeS1Group;
import ltd.kcdevice.view.EqSelectS1Group;
import ltd.kcdevice.view.ViewHolder;
import ltd.kcdevice.view.VolumeS1Group;
import main.MAPI;

import static main.MAPI.MSTRING;

public class Kc35hPage extends CommonPage {
    private StatusS1Group mStatusS1Group;
    private VolumeS1Group mVolumeS1Group;           // 音量调节页面
    private InputSourceS1Group mInputSourceS1Group;             // 输入调节页面
    private EqSelectS1Group mEqSelectS1Group;               // 音效调节页面
    private ListenModeS1Group mListenModeS1Group;       // 立体声和多声道调节页面
    private PlayS1Group mPlayS1Group;               // 播放调节页面
    private MicroPhoneS1Group mMicroPhoneS1Group;   // 麦克风调节页面
    private MoreS1Group mMoreS1Group;              // 更多调节页面 噪音测试页面 喇叭设置页面 亮度设置 夜间模式
    private SetupS1Group mSetupS1Group;            // 设置调节页面


    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }
    @Override
    public void onInitView(){                       // 使用者需要继承，初始化页面控件等显示
        setLayout(R.layout.page_kc35h);
        BDevice bDevice = null;
        String macAddr = null;
        if (mBaseInput != null) {                   // 可能仅仅需要刷新，未必是重新开始的
            bDevice = mBaseInput.getInput(0);
            macAddr = mBaseInput.getInput(1);
        }
        mBaseInput = null;                      // 输入参数的使命已经完成，记得及时销毁之

        mKcHeaderView = (KcHeaderView) findViewById(R.id.khvKc35h);
        mKcHeaderView.setTitle("KC35H", new KcTypeListener() {
            @Override
            public void onMessage(int type) {
                switch (type) {
                    case KcHeaderView.TYPE_CLICK_LEFT:
                        setNext();
                        break;
                    case KcHeaderView.TYPE_CLICK_RIGHR:
                        break;
                }
            }
        });
        mStatusS1Group = findViewById(R.id.statusKc35h);

        mVolumeS1Group = new VolumeS1Group(MAPI.mActivity, null);
        mInputSourceS1Group = new InputSourceS1Group(MAPI.mActivity, null);
        mEqSelectS1Group = new EqSelectS1Group(MAPI.mActivity, null);
        mListenModeS1Group = new ListenModeS1Group(MAPI.mActivity, null);
        mPlayS1Group = new PlayS1Group(MAPI.mActivity, null);
        mMicroPhoneS1Group = new MicroPhoneS1Group(MAPI.mActivity, null);
        mSetupS1Group = new SetupS1Group(MAPI.mActivity, null);
        mMoreS1Group = new MoreS1Group(MAPI.mActivity, null);

        initListView();
        startDevice(bDevice, macAddr);
    }
    @Override
    public void onInitData(){                               // 初始化数据，总是在初始化控件后300毫秒被调用，实现先出来页面再出来真实数据分开，防止数据多时卡
        showInfo();
    }
    @Override
    public void onPageUpdate(BasePage basePage){    // 更新显示页面数据，用于刷新显示 basePage返回上面的页面，可以使用getPageResult()获得返回的内容
    }
    @Override
    public void onFinish(){                         // 使用者选择性继承，用户按了后退键，不继承就直接关闭了
        setNext();
    }
    @Override
    public void showInfo(){
        BDevice bDevice = MKCDEV.getRunBDevice();
        mStatusS1Group.showInfo(bDevice);

        int value = MKCDEV.getRmAudCtr(bDevice, Kc3xType.KCM_VOLUME_CTRL);
        mVolumeS1Group.setVolume(value);
        value = MKCDEV.getRmAudCtr(bDevice, Kc3xType.KCM_VOLUME_MAX);
        mVolumeS1Group.setMaxVolume(value);

        value = MKCDEV.getRmAudCtr(bDevice, Kc3xType.KCM_INPUT_SOURCE);
        mInputSourceS1Group.setSelect(value);

        value = MKCDEV.getRmAudCtr(bDevice, Kc3xType.KCM_LISTEN_MODE);
        mListenModeS1Group.setSelect(value);

        value = MKCDEV.getRmAudCtr(bDevice, Kc3xType.KCM_EQ_SELECT);
        mEqSelectS1Group.setSelect(value);

        ProductInfo productInfo = new ProductInfo();
        String t1 = productInfo.getDateTime(bDevice, ProductInfo.PRODUCT_DATE_VER + 0);
        String t2 = productInfo.getVersion(bDevice, ProductInfo.PRODUCT_DATE_VER + 0);
        String t3 = productInfo.getDateTime(bDevice, ProductInfo.PRODUCT_DATE_VER + 8);
        String t4 = productInfo.getVersion(bDevice, ProductInfo.PRODUCT_DATE_VER + 8);
        String t5 = productInfo.getDateTime(bDevice, ProductInfo.PRODUCT_DATE_VER + 16);
        String t6 = productInfo.getVersion(bDevice, ProductInfo.PRODUCT_DATE_VER + 16);
        MLOG(String.format("固件 (%s)(%s)(%s)(%s)(%s) ", t1, t2, t3, t4, t5));
        if (productInfo.isKc35h(bDevice)) {
            MLOG(String.format("固件 Kc35h_A_V%s(%s) Kc35h_B_V%s(%s) WfBtKc3x_V%s(%s) ", t1, t2, t3, t4, t5, t6));
            t1 = productInfo.getBrand(bDevice);
            t2 = productInfo.getModel(bDevice);
            t3 = productInfo.getNickName(bDevice);
            t4 = productInfo.getTxWifiName(bDevice);
            t5 = productInfo.getTrueWifi(bDevice);
            MLOG(String.format("固件文字节 (%s)(%s)(%s)(%s)(%s) ", t1, t2, t3, t4, t5));
        }

    }

    public final  static int LIST_LINE = 8;                     // 决定本页列表的行数
    private void initListView(){
        View view = findViewById(R.id.rvKc35h);                // 列表的布局
        mAdapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ViewHolder holder = new ViewHolder(LayoutInflater.from(MAPI.mActivity).inflate(R.layout.item_common_layout, parent, false));
                FrameLayout flCommon = holder.itemView.findViewById(R.id.flCommon);
                switch (viewType){
                    case 0: flCommon.addView(mVolumeS1Group); break;// 音量调节页面
                    case 1: flCommon.addView(mInputSourceS1Group); break;// 输入调节页面
                    case 2: flCommon.addView(mEqSelectS1Group); break;// 音效调节页面
                    case 3: flCommon.addView(mListenModeS1Group); break;// 立体声和多声道调节页面
                    case 4: flCommon.addView(mPlayS1Group); break;// 播放调节页面
                    case 5: flCommon.addView(mMicroPhoneS1Group); break;// 麦克风调节页面
                    case 6: flCommon.addView(mMoreS1Group); break;  // 更多调节页面 噪音测试页面 喇叭设置页面 亮度设置 夜间模式
                    case 7: flCommon.addView(mSetupS1Group); break;// 设置调节页面
                }
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                switch (position){
                case 0:
//                    mVolumeS1Group.setVolume(23);
                    break;
                case 1:
//                    MLOG("Kc35hPage ViewHolder "+viewHolder);
//                    MLOG("Kc35hPage A "+holder.itemView);
//                    StatusS1Group statusS1Group = (StatusS1Group)viewHolder.mView;
//
                    break;
                }
            }
            @Override
            public int getItemCount() {
                return LIST_LINE;
            }
            @Override
            public int getItemViewType(int position) {
                return position;                                    // 可以根据行数据决定viewType
            }
        };
        initListView(view);
    }

}

