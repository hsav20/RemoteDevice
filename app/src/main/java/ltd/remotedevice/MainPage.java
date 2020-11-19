package ltd.remotedevice;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kcbsdk.AUserBase;
import com.kcbsdk.EventHandler;
import com.kcbsdk.MyWorks.AFileInfo;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ltd.advskin.MRAM;
import ltd.advskin.MSKIN;
import ltd.advskin.VARIA;
import ltd.advskin.base.BasePage;
import ltd.advskin.base.KcTwoListener;
import ltd.advskin.base.KcTypeListener;
import ltd.advskin.base.MPAGE;
import ltd.advskin.beandbs.BLocalFileInfo;
import ltd.advskin.task.LocalLookup;
import ltd.advskin.utils.AdvView;
import ltd.advskin.view.KcHeaderView;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.device.KcmCtrl;
import ltd.kcdevice.device.DevUtils;
import ltd.kcdevice.device.KcmText;
import ltd.kcdevice.device.RunBle;
import ltd.kcdevice.model.CommonPage;
import ltd.kcdevice.page.DeviceCenterPage;
import ltd.kcdevice.page.DeviceSearchPage;
import ltd.kcdevice.model.Kc35hPage;
import ltd.kcdevice.page.SpeakerSetupPage;
import ltd.kcdevice.view.InputSourceS1Group;
import ltd.kcdevice.view.MicroPhoneS1Group;
import ltd.kcdevice.view.MoreS1Group;
import ltd.kcdevice.view.PlayS1Group;
import ltd.kcdevice.view.SetupS1Group;
import ltd.kcdevice.view.SpeakerSetupView;
import ltd.kcdevice.view.ListenModeS1Group;
import ltd.kcdevice.view.EqSelectS1Group;
import ltd.kcdevice.view.VolumeS1Group;
import main.AsmUtils;
import main.MAPI;

import static ltd.advskin.MRAM.mMyTools;
import static ltd.advskin.MRAM.mMyUtils;
import static main.MAPI.MSTRING;
import static main.MAPI.MTOAST;

public class MainPage extends BasePage {
    private long mBackPressedTime;
    private TextView tvLocalObbligInfo;
    private TextView tvLocalPhotoInfo;
    private TextView tvTips;
    private String gsVersin = "remotedevice";
    private ImageView mImageView;
    private Timer mTimer;
    private Timer mAutoKey;

    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    @Override
    public void onInitView() {
//        setLayout(R.layout.activity_main);
        setLayout(R.layout.activity_main_a, R.layout.activity_main_1, R.layout.activity_main_2);

        mKcHeaderView = findViewById(R.id.khvMainPage);
        mKcHeaderView.setTitle("MainPage", new KcTypeListener() {
            @Override
            public void onMessage(int type) {
                switch (type) {
                    case KcHeaderView.TYPE_CLICK_LEFT:
                        onFinish();
                        break;
                }
            }
        });

        ((Button)findViewById(R.id.btnKey1)).setOnClickListener(onClickListener);
        ((Button)findViewById(R.id.btnKey2)).setOnClickListener(onClickListener);
        ((Button)findViewById(R.id.btnKey3)).setOnClickListener(onClickListener);
        ((Button)findViewById(R.id.btnKey4)).setOnClickListener(onClickListener);
        ((Button)findViewById(R.id.btnKey5)).setOnClickListener(onClickListener);
        ((Button)findViewById(R.id.btnKey6)).setOnClickListener(onClickListener);
        ((Button)findViewById(R.id.btnKey7)).setOnClickListener(onClickListener);
        ((Button)findViewById(R.id.btnKey8)).setOnClickListener(onClickListener);
        ((Button)findViewById(R.id.btnKey9)).setOnClickListener(onClickListener);
        ((Button)findViewById(R.id.btnKeya)).setOnClickListener(onClickListener);
        ((Button)findViewById(R.id.btnKeyb)).setOnClickListener(onClickListener);
        ((Button)findViewById(R.id.btnKeyc)).setOnClickListener(onClickListener);
        ((TextView)findViewById(R.id.tvKey00)).setOnClickListener(onClickListener);
        ((TextView)findViewById(R.id.tvKey01)).setOnClickListener(onClickListener);
        ((TextView)findViewById(R.id.tvKey02)).setOnClickListener(onClickListener);
        ((TextView)findViewById(R.id.tvKey03)).setOnClickListener(onClickListener);

        ((Button)findViewById(R.id.btnKeyd)).setOnClickListener(onClickListener);
        ((Button)findViewById(R.id.btnKeye)).setOnClickListener(onClickListener);
        ((Button)findViewById(R.id.btnKeyf)).setOnClickListener(onClickListener);
        ((Button)findViewById(R.id.btnKeyg)).setOnClickListener(onClickListener);
        ((Button)findViewById(R.id.btnKeyh)).setOnClickListener(onClickListener);
        ((Button)findViewById(R.id.btnKeyi)).setOnClickListener(onClickListener);


        showSkinMode();

        tvLocalObbligInfo = (TextView) findViewById(R.id.tvLocalObbligInfo);
        tvLocalPhotoInfo = (TextView) findViewById(R.id.tvLocalPhotoInfo);
        tvTips = (TextView) findViewById(R.id.tvTips);
        tvTips.setText(gsVersin + MPAGE.getBasePageTips());

        MKCDEV.stopDevice();
    };

    @Override
    public void onInitData(){                       // 初始化数据，总是在初始化控件后300毫秒被调用，实现先出来页面再出来真实数据分开，防止数据多时卡
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                MAPI.mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MKCDEV.onTimer();
                        MSKIN.onTimer();
                    }
                });
            }
        }, 300, 300);
        mMyTools.setUiEventCallBack(new EventHandler() {
            @Override
            public void onComplete(int methodType, boolean isFail, String message){
                if (methodType == 0){
                    if (mMyTools.isEventDynamicIP()){
                        MRAM.gServerNumber = mMyUtils.getServerNumber();
//                        MSTRING("MainActivity 服务 器 中断产生 "+MRAM.gServerNumber);
                        AdvView.setDebugText(String.format("%d", MRAM.gServerNumber));
                    }
                    if (mMyTools.isEventNeedReLogin()){
                        MSTRING("MainActivity 强制退出账号 中断产生 A");
                    }
                    if (mMyTools.isEventExamine()){
                        MSTRING("MainActivity 验证服务 中断产生");
                    }
                    if (mMyTools.isEventSystemTime()){
                        MSTRING("isEventSystemTime "+mMyUtils.getSystemIniTime());
                    }
                }
            }
        });
        mAutoKey = new Timer();
        mAutoKey.schedule(new TimerTask() {
            @Override
            public void run() {
                MAPI.mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (true) {
//                            MKCDEV.startKc35hPage(null, "50:02:91:86:CA:56");
//                            MKCDEV.startKc35hPage(null, "6A:C8:8E:42:36:F3");
                        }
                        mAutoKey.cancel();
                        mAutoKey = null;
                    }
                });
            }
        }, 900);
    }
    @Override
    public void onPageUpdate(BasePage basePage){    // 更新显示页面数据，用于刷新显示 basePage返回上面的页面，可以使用getPageResult()获得返回的内容
        MSTRING(String.format("MainPage onPageUpdate A %s", basePage != null ? basePage.getClass().getName() : "null"));
    }
    @Override
    public void onFinish(){
        MSTRING("MainPage onFinish A ");
        if (mBackPressedTime == 0) {
            mBackPressedTime = System.currentTimeMillis();
            MTOAST(MAPI.mActivity.getString(R.string.tip_back_pressed));
        } else {
            if ((System.currentTimeMillis() - mBackPressedTime) < 2000) {
                if (mTimer != null){
                    mTimer .cancel();
                }
                MSKIN.powerOff();
                setClose();
                MPAGE.mPageActivity.finish();
            }
            mBackPressedTime = 0;
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//        MSTRING("MainActivity onClick");
            AsmUtils asmUtils = new AsmUtils();
            AUserBase aUserBase = new AUserBase();
//            aUserBase.CloudType = (byte)AWorksType.WORKS_TYPE_APP21.getType();
            aUserBase.UserID = 5130804;
            int g4Local_1 = 0;
            int type;
            AFileInfo aFileInfo = new AFileInfo();
//            aFileInfo.CloudType = (byte)AWorksType.WORKS_TYPE_APP21.getType();
            aFileInfo.UserID = 33;

            String gsLocal_1 = "";
            switch (view.getId()) {
                case R.id.btnKey1:      // 电器中心
                    MKCDEV.stopDevice();
                    MKCDEV.startDeviceCenterPage();
                    break;
                case R.id.btnKey2:      // 查找电器
                    MKCDEV.stopDevice();
//                    MKCDEV.mBluetooth = null;
                   MKCDEV.startDeviceSearch();
                    break;
                case R.id.btnKey3:      // KC35H
                    MKCDEV.startKc35hPage(null, "50:02:91:86:CA:56");
//                    MKCDEV.startKc35hPage(null, "6F:F1:B2:FC:45:62");
//                    MKCDEV.startKc35hPage(null, "75:74:F3:54:B2:04");
//                    MKCDEV.startKc35hPage(null, "41:87:D2:F0:07:94");

//                    MKCDEV.startKc35hPage(null, "24:0A:C4:2A:60:C6");
                    break;
                case R.id.btnKey4:      // MIC
                    MKCDEV.startMicDetailAdjPage();
                    break;
                case R.id.btnKey5:      // 噪音测试
                    MKCDEV.startNoiseTestPage();
                    break;

                case R.id.btnKey6:          // 黑色主题
                    MKCDEV.addTheme();
                    ltd.remotedevice.MAPP.isChangThemeStyle = true;
                    getActivity().finish();
                    getActivity().startActivity(new Intent(getActivity(), PageActivity.class));
                    break;
                case R.id.btnKey7:      // 噪音测试
                    MKCDEV.startSpeakerSetupPage();
                    break;
            }
        }
    };
    private LocalLookup mLocalLookup;
    private int gCleanTempSize;
    private int gCleanTempQty;
    private int gCleanTempStep;
    private void clearTempFile(){
        //                    MSTRING("清除缓存 A ");
        VARIA.mUserHead = null;  // 清理头像的记忆
        VARIA.mWorksCover = null; // 清理头像的记忆
        mLocalLookup = new LocalLookup();
        mLocalLookup.setListener(new KcTwoListener() {
            @Override
            public void onMessage(Object object1, Object object2) {
//                MSKIN.delDbsAllItem(VARIA.DBS_PHOTO_SERVER, 0);
//                MSKIN.delDbsAllItem(VARIA.DBS_COMMENT, 0);
                List<BLocalFileInfo> lists = (List)object2;
                int length = lists.size();
//                            MSTRING("清除缓存 B "+length);
                String gsLocal_1;
                for (int counter = 0; counter < length; counter++) {
                    gsLocal_1 = lists.get(counter).FileName;
                    MSTRING(String.format("CacheActivity onMessage A %d %s ", counter,  gsLocal_1));
                    gCleanTempSize += MAPI.GET_FILE_LENGTH(gsLocal_1);
                    if (gsLocal_1.indexOf("/lib/") < 1) {                 // 删除除了/lib/里面的文件
                        MAPI.DELETE_FILE(gsLocal_1);
                    }
                }
                gCleanTempQty += length;
                switch (++gCleanTempStep){
                    case 1:
                        String path = MAPI.mActivity.getCacheDir().getPath() + "/";
                        path = path.replace("/cache/", "/");
                        mLocalLookup.setRootPath(path, null);
                        MSTRING(String.format("CacheActivity gCleanTempQty A %s ", path ));
                        mLocalLookup.getFileList(Integer.MAX_VALUE, null, null, new String[] {"cache"});
                        break;
                    case 2:
                        path = MAPI.mActivity.getCacheDir().getPath() + "/";
                        path = path.replace("/cache/", "/");
                        mLocalLookup.setRootPath(path, null);
                        MSTRING(String.format("CacheActivity gCleanTempQty B %s ", path ));
                        mLocalLookup.getFileList(Integer.MAX_VALUE, null, null, new String[] {"databases", "cache"} );
                        break;
                    case 3:
                        MTOAST(String.format("已经清除文件%d个共%s，请重新启动！", gCleanTempQty, mMyUtils.formatFileSize(gCleanTempSize)), 5000);
                        break;
                }
            }
        });
        gCleanTempStep = 0;
        gCleanTempSize = 0;
        gCleanTempQty = 0;

//        mLocalLookup.setRootPath(MOBBW.getFilePath(), null);
        mLocalLookup.setDataPath();
        mLocalLookup.getFileList(Integer.MAX_VALUE, null, null, null);
    }

    private void showSkinMode(){
        String[] text = {"白色主题", "黑色主题", "粉色主题", "蓝色主题"};
        ((Button)findViewById(R.id.btnKey6)).setText(text[MKCDEV.getThemeStyle() % text.length]);
    }
    public void initKcbData(){
        mMyTools.setUiEventCallBack(new EventHandler() {
            @Override
            public void onComplete(int methodType, boolean isFail, String message){
                if (methodType == 0){
                    if (mMyTools.isEventDynamicIP()){
                        MRAM.gServerNumber = mMyUtils.getServerNumber();
                        MSTRING("MainPage 服务器 中断产生 "+MRAM.gServerNumber);
                        AdvView.setDebugText(String.format("%d", MRAM.gServerNumber));
                    }
                    if (mMyTools.isEventNeedReLogin()){
                        MSTRING("MainPage 强制退出账号 中断产生");
                    }
                    if (mMyTools.isEventExamine()){
                        MSTRING("MainPage 验证服务 中断产生");
                    }
                    if (mMyTools.isEventSystemTime()){
                        MSTRING("isEventSystemTime "+mMyUtils.getSystemIniTime());
                    }
                }
            }
        });

    }
}
