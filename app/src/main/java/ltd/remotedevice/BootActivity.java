package ltd.remotedevice;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.tencent.bugly.crashreport.CrashReport;

import ltd.advskin.MSKIN;
import ltd.advskin.VARIA;
import ltd.advskin.base.KcListener;
import ltd.advskin.base.SkinActivity;
import ltd.advskin.permission.PermissionsActivity;
import ltd.advskin.permission.PermissionsChecker;
import ltd.advskin.task.TaskSysUpdate;
import ltd.advskin.utils.SysTools;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.device.DevUtils;
import ltd.kcdevice.device.KcmCtrl;
import ltd.kcdevice.device.KcmText;
import ltd.kcdevice.device.RunBle;
import ltd.kcdevice.model.CommonPage;
import ltd.kcdevice.model.Kc35hPage;
import ltd.kcdevice.page.DeviceCenterPage;
import ltd.kcdevice.page.DeviceModifyPage;
import ltd.kcdevice.page.DeviceSearchPage;
import ltd.kcdevice.page.SetupPage;
import ltd.kcdevice.page.SpeakerSetupPage;
import ltd.kcdevice.page.VersionInfoPage;
import ltd.kcdevice.view.EqSelectS1Group;
import ltd.kcdevice.view.InputSourceS1Group;
import ltd.kcdevice.view.ListenModeS1Group;
import ltd.kcdevice.view.MicroPhoneS1Group;
import ltd.kcdevice.view.MoreS1Group;
import ltd.kcdevice.view.PlayS1Group;
import ltd.kcdevice.view.SetupS1Group;
import ltd.kcdevice.view.SpeakerSetupView;
import ltd.kcdevice.view.VolumeS1Group;
import main.MAPI;

import static main.MAPI.MSTRING;
import static main.MAPI.MTOAST;


public class BootActivity extends SkinActivity {
    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    private void enableLog(){
        MainPage.logEnable = true;

        MLOG("这里打开所有需要测试的LOG");
        MainPage.logEnable = true;
        DevUtils.logEnable = true;
        DeviceCenterPage.logEnable = true;
        DeviceSearchPage.logEnable = true;
        Kc35hPage.logEnable = true;
        CommonPage.logEnable = true;
        DeviceModifyPage.logEnable = true;
        SetupPage.logEnable = true;
        VersionInfoPage.logEnable = true;

        KcmCtrl.logEnable = true;
        KcmText.logEnable = true;
        RunBle.logEnable = true;
        VolumeS1Group.logEnable = true;
        InputSourceS1Group.logEnable = true;
        EqSelectS1Group.logEnable = true;
        ListenModeS1Group.logEnable = true;
        PlayS1Group.logEnable = true;
        MicroPhoneS1Group.logEnable = true;
        SetupS1Group.logEnable = true;
        MoreS1Group.logEnable = true;
        SpeakerSetupPage.logEnable = true;
        SpeakerSetupView.logEnable = true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);
        MAPI.mActivity = this;
        MAPI.mContext = this;
        enableLog();
        CrashReport.initCrashReport(getApplicationContext(), "29b384d46d", true);    // 注册时申请的APP ID
        SysTools sysTools = new SysTools();
        sysTools.setSharedString("SysTools.moduleName", getString(R.string.app_name));
        sysTools.setSharedString("SysTools.appName", "cnkc");

        MSTRING("BootActivity onCreate AA");
        getBasePermissions();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x99) {                  // 权限申请
            if (PermissionsActivity.PERMISSIONS_DENIED == resultCode) {             // 权限未被授予，退出应用
                if (VARIA.mSaveKcListener != null) {
                    VARIA.mSaveKcListener.onMessage(PermissionsActivity.PERMISSIONS_DENIED);
                }
            } else if (PermissionsActivity.PERMISSIONS_GRANTED == resultCode) {     // 权限被授予
                if (VARIA.mSaveKcListener != null){
                    VARIA.mSaveKcListener.onMessage(null);
                }
            }
            VARIA.mSaveKcListener = null;
        }
//
//        if (requestCode == 0x99) {                  // 权限申请
//            if (PermissionsActivity.PERMISSIONS_DENIED == resultCode) {             // 权限未被授予，退出应用
//                finish();
//            } else if (PermissionsActivity.PERMISSIONS_GRANTED == resultCode) {     // 权限被授予
//                permissionsSucess();
//            }
//        }
    }
    private void getBasePermissions(){
        PermissionsChecker checker = new PermissionsChecker();
        String[] PERMISSIONS = checker.getBasePermissions();
        if (checker.lacksPermissions(PERMISSIONS)) {
            MSKIN.startPermissionsActivity(PERMISSIONS, new KcListener() {
                @Override
                public void onMessage(Object object) {
                    if (object == null){
                        permissionsSucess();
                    }else {
                        MTOAST("");
                    }
                }
            });
        }else {
            permissionsSucess();
        }
    }


    private void permissionsSucess(){
        // fixMode 0x0000=不强制固定的IP地址(正常)，
        // B15：1=使用设定的IP，B14：1=gsServerIP使用设定0=使用编号 B13：1=gsFileServerIP使用设定0=使用编号
        // B12-B8 gsServerIP编号 B7-0 gsServerIP编号
//        int fixMode = 0xe100; // 0x8102 168 239
//        String fixIPAddr = "192.168.0.231";
//        int fixMode = 0x8102; // 0x8102 168 239
//        String fixIPAddr = "114.215.194.168";
        int fixMode = 0;
        String fixIPAddr = "";
        if (true){
            fixMode = 0x8101;       // 232
            fixMode = 0x8100;       // 231
            fixMode = 0x8102;       // 0x8102 168 239
            fixIPAddr = "114.215.194.168";
        }
        MKCDEV.setStartUp(fixMode, fixIPAddr);
//        MSTRING("BootActivity onCreate B "+ MRAM.gsDownloadPath);
//        TaskSysUpdate taskSysUpdate = new TaskSysUpdate("cnkc");
//        if (true) {
//            taskSysUpdate.fileFromAssets();
//        }else {
//            taskSysUpdate.fileFromServer(null,null);
//        }

//        MSTRING("BootActivity permissionsSucess "+MAPI.GET_FILE_LENGTH(gsLocal_1 + "c3icon.hsd")+" "+MAPI.GET_FILE_LENGTH(gsLocal_1 + "pngphoto.hsd"));
        if (true) {
            String gsLocal_1 = Build.MODEL + "&" + Settings.Secure.getString(MAPI.mContext.getContentResolver(), "android_id");
            MSTRING("String gsLocal_1A  "+gsLocal_1);
            startActivity(new Intent(BootActivity.this, PageActivity.class));
        }else {
//            startActivity(new Intent(BootActivity.this, MainActivity.class));
        }
        finish();
    }


}
