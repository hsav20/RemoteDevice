package ltd.remotedevice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.kcbsdk.EventHandler;

import ltd.advskin.MRAM;
import ltd.advskin.MSKIN;
import ltd.advskin.VARIA;
import ltd.advskin.base.BasePage;
import ltd.advskin.base.MPAGE;
import ltd.advskin.base.SkinActivity;
import ltd.advskin.permission.PermissionsActivity;
import ltd.advskin.utils.AdvView;
import main.MAPI;

import static com.kcbsdk.wpapper.KCB_LIST_MSG_WITHDRAW;
import static ltd.advskin.MRAM.mMyTools;
import static ltd.advskin.MRAM.mMyUtils;
import static main.MAPI.MSTRING;

public class MainActivity extends SkinActivity {
    private boolean isPageCreate;

    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        MAPI.mActivity = this;
        MAPI.mContext = this;
        AdvView.checkHorizontal();

        // 垂直方向/水平方向使用的ViewGroup不相同
        MPAGE.setBasePage(this, (FrameLayout) findViewById(R.id.flPage));

        startPageMain();
        initKcbData();
    }
    @Override
    public void onBackPressed() {
        MLOG("PageActivity onBackPressed \n"+ MPAGE.getBasePageTips());
        if (MPAGE.mPageList != null && MPAGE.mPageList.size() > 0){
            BasePage basePage = MPAGE.mPageList.get(MPAGE.mPageList.size() - 1);
            basePage.onFinish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)	{
        super.onActivityResult(requestCode, resultCode, data);
        MLOG(String.format("onActivityResult A %02x", requestCode));
        if (requestCode == 0x99) {                  // 权限申请
            if (PermissionsActivity.PERMISSIONS_DENIED == resultCode) {             // 权限未被授予，退出应用
                VARIA.mSaveKcListener.onMessage(PermissionsActivity.PERMISSIONS_DENIED);
//                finish();
            } else if (PermissionsActivity.PERMISSIONS_GRANTED == resultCode) {     // 权限被授予
                if (VARIA.mSaveKcListener != null){
                    VARIA.mSaveKcListener.onMessage(null);
                }
//                permissionsSucess();
            }
            VARIA.mSaveKcListener = null;
        }
    }
    public void startPageMain(){
        BasePage basePage;
        if (false){
            basePage = new MainPage();
        }else {
            basePage = new TestPage();
        }

        if (MPAGE.mPageCurrent == null) {
            MLOG(String.format("PageActivity startPageMain A"));
            MPAGE.startBasePage(basePage);
        }else {
            MLOG(String.format("PageActivity startPageMain B %s %s", MAPP.isChangThemeStyle, MPAGE.mPageCurrent.getClass().getName()));
            if (MAPP.isChangThemeStyle){       // 更换主题
                MAPP.isChangThemeStyle = false;
                MPAGE.mPageList = null;
                MPAGE.mPageCurrent = null;
                MPAGE.startBasePage(basePage);
                MLOG("PageActivity startPageMain C \n"+ MPAGE.mPageList + " - "+MPAGE.getBasePageTips());
            }else {
                MPAGE.setInitView(MPAGE.mPageCurrent);
            }

        }
    }
    public void initKcbData() {
        MSKIN.checkMainInit();                              // 死机后防止空指针
        mMyTools.setUiEventCallBack(new EventHandler() {
            @Override
            public void onComplete(int methodType, boolean isFail, String message){
                if (methodType == 0){
                    if (mMyTools.isEventDynamicIP()) {
                        int gServerNumber = mMyUtils.getServerNumber();
                        MLOG("MainActivity 服务器切换 中断产生 " + gServerNumber);
                    }
                    if (mMyTools.isEventNeedReLogin()) {
                        MLOG("MainActivity 强制退出账号 中断产生 B");
                    }
                    if (mMyTools.isEventExamine()) {
                        MLOG("MainActivity 验证服务 中断产生");
                    }
                    if (mMyTools.isEventSystemTime()) {
                        MLOG("isEventSystemTime 系统时间");
                        MAPP.isEventSystem = true;
                        mMyUtils.getSystemIniTime();
                        int type = ( 2 * 4 ) | (0 << 24);               // 第0层 第2位置
                        int time  = mMyTools.getDifferentTime(type);
                        mMyTools.setDifferentRefreshed(type, time);
                    }
                    if(mMyTools.isEventAllMsgCount()) {
                        MLOG("isEventAllMsgCount 有新消息");
                            if (MAPP.gSystemType == -1) {
                                MAPP.gSystemType = mMyTools.getDifferentType();
                                MLOG(String.format("systemOnTimer gSystemType %x", MAPP.gSystemType));
                                if (MAPP.gSystemType != -1) {
                                    MAPP.gSystemTime = mMyTools.getDifferentTime(MAPP.gSystemType);
                                    MLOG(String.format("systemOnTimer gSystemTime %x", MAPP.gSystemTime));
                                }
                            } else {
                                int area = MAPP.gSystemType >> 24;
                                int offset = MAPP.gSystemType & 0xffffff;
                                MLOG(String.format("读取消息 %x %x %d %d", MAPP.gSystemType, MAPP.gSystemTime, area, offset));
                                if (area == 0){
                                    if (offset == (2 * 4)){ // SystemIni
                                        MLOG("systemOnTimer isSystemIni ");
//                                        mMyUtils.getSystemIniTime();
                                        mMyTools.setDifferentRefreshed(MAPP.gSystemType, MAPP.gSystemTime);
                                    }
                                    else if(offset == (6 * 4)) {    // 消息总数
                                        MLOG("systemOnTimer 消息总数 完成");
                                        mMyTools.setDifferentRefreshed(MAPP.gSystemType, MAPP.gSystemTime);
                                    }
                                    else if(offset == (7 * 4)) {    // Chat
                                        MLOG("systemOnTimer 聊天 完成");
                                        mMyTools.setDifferentRefreshed(MAPP.gSystemType, MAPP.gSystemTime);
                                    }
                                } else {                    // msgSub
                                    MLOG("systemOnTimer MSGSub子消息 成功 ");
                                    mMyTools.setDifferentRefreshed(MAPP.gSystemType, MAPP.gSystemTime);
                                }
                                MAPP.gSystemType = -1;
                            }
                    }
                    if(mMyTools.isEventChat()) {
                        MLOG("isEventChat 聊天有消息");
                        MAPP.isEventChat = true;
                        int chatType = ( 7 * 4 ) | (0 << 24);                           // 第0层 第7位置
                        MAPP.gChatTime = mMyTools.getDifferentTime(chatType);
                        MLOG("systemOnTimer 聊天 完成");
                        mMyTools.setDifferentRefreshed(chatType, MAPP.gChatTime);
                    }
                    if (mMyTools.isEventWithdraw()){
                        MAPP.isEventWithdraw = true;
                        MLOG("isEventWithdraw 撤回有消息");
                        int type = ( KCB_LIST_MSG_WITHDRAW * 4 ) | (1 << 24);           // 第1层 KCB_LIST_MSG_WITHDRAW位置
                        int time = mMyTools.getDifferentTime(type);
                        MLOG("systemOnTimer 撤回  完成");
                        mMyTools.setDifferentRefreshed(type, time);
                    }
                }
            }
        });
    }
}
