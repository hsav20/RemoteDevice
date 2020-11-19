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
import static main.MAPI.MERROR;
import static main.MAPI.MSTRING;

public class PageActivity extends SkinActivity {
    private boolean isPageCreate;
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
        MSTRING("PageActivity onBackPressed \n"+ MPAGE.getBasePageTips());
        if (MPAGE.mPageList != null && MPAGE.mPageList.size() > 0){
            BasePage basePage = MPAGE.mPageList.get(MPAGE.mPageList.size() - 1);
            basePage.onFinish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)	{
        super.onActivityResult(requestCode, resultCode, data);
        MSTRING(String.format("onActivityResult A %02x", requestCode));
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
        if (MPAGE.mPageCurrent == null) {
            MSTRING(String.format("PageActivity startPageMain A"));
            MPAGE.startBasePage(new MainPage());
        }else {
            MSTRING(String.format("PageActivity startPageMain B %s %s", MAPP.isChangThemeStyle, MPAGE.mPageCurrent.getClass().getName()));
            if (MAPP.isChangThemeStyle){       // 更换主题
                MAPP.isChangThemeStyle = false;
                MPAGE.mPageList = null;
                MPAGE.mPageCurrent = null;
                MPAGE.startBasePage(new MainPage());
                MSTRING("PageActivity startPageMain C \n"+ MPAGE.mPageList + " - "+MPAGE.getBasePageTips());
            }else {
                MPAGE.setInitView(MPAGE.mPageCurrent);
            }

        }
//        if (!isPageCreate) {
//            isPageCreate = true;
//            MPAGE.startBasePage(new MainPage());
//        }else {
//
//        }
    }
    public void initKcbData() {
        MSKIN.checkMainInit();                              // 死机后防止空指针
        mMyTools.setUiEventCallBack(new EventHandler() {
            @Override
            public void onComplete(int methodType, boolean isFail, String message){
                if (methodType == 0){
                    if (mMyTools.isEventDynamicIP()) {
                        int gServerNumber = mMyUtils.getServerNumber();
                        MSTRING("MainActivity 服务器切换 中断产生 " + gServerNumber);
                    }
                    if (mMyTools.isEventNeedReLogin()) {
                        MSTRING("MainActivity 强制退出账号 中断产生 B");
                    }
                    if (mMyTools.isEventExamine()) {
                        MSTRING("MainActivity 验证服务 中断产生");
                    }
                    if (mMyTools.isEventSystemTime()) {
                        MSTRING("isEventSystemTime 系统时间");
                        MAPP.isEventSystem = true;
                        mMyUtils.getSystemIniTime();
                        int type = ( 2 * 4 ) | (0 << 24);               // 第0层 第2位置
                        int time  = mMyTools.getDifferentTime(type);
                        mMyTools.setDifferentRefreshed(type, time);
                    }
                    if(mMyTools.isEventAllMsgCount()) {
                        MSTRING("isEventAllMsgCount 有新消息");
                            if (MAPP.gSystemType == -1) {
                                MAPP.gSystemType = mMyTools.getDifferentType();
                                MSTRING(String.format("systemOnTimer gSystemType %x", MAPP.gSystemType));
                                if (MAPP.gSystemType != -1) {
                                    MAPP.gSystemTime = mMyTools.getDifferentTime(MAPP.gSystemType);
                                    MSTRING(String.format("systemOnTimer gSystemTime %x", MAPP.gSystemTime));
                                }
                            } else {
                                int area = MAPP.gSystemType >> 24;
                                int offset = MAPP.gSystemType & 0xffffff;
                                MSTRING(String.format("读取消息 %x %x %d %d", MAPP.gSystemType, MAPP.gSystemTime, area, offset));
                                if (area == 0){
                                    if (offset == (2 * 4)){ // SystemIni
                                        MSTRING("systemOnTimer isSystemIni ");
//                                        mMyUtils.getSystemIniTime();
                                        mMyTools.setDifferentRefreshed(MAPP.gSystemType, MAPP.gSystemTime);
                                    }
                                    else if(offset == (6 * 4)) {    // 消息总数
                                        MSTRING("systemOnTimer 消息总数 完成");
                                        mMyTools.setDifferentRefreshed(MAPP.gSystemType, MAPP.gSystemTime);
                                    }
                                    else if(offset == (7 * 4)) {    // Chat
                                        MSTRING("systemOnTimer 聊天 完成");
                                        mMyTools.setDifferentRefreshed(MAPP.gSystemType, MAPP.gSystemTime);
                                    }
                                } else {                    // msgSub
                                    MSTRING("systemOnTimer MSGSub子消息 成功 ");
                                    mMyTools.setDifferentRefreshed(MAPP.gSystemType, MAPP.gSystemTime);
                                }
                                MAPP.gSystemType = -1;
                            }
                    }
                    if(mMyTools.isEventChat()) {
                        MSTRING("isEventChat 聊天有消息");
                        MAPP.isEventChat = true;
                        int chatType = ( 7 * 4 ) | (0 << 24);                           // 第0层 第7位置
                        MAPP.gChatTime = mMyTools.getDifferentTime(chatType);
                        MSTRING("systemOnTimer 聊天 完成");
                        mMyTools.setDifferentRefreshed(chatType, MAPP.gChatTime);
                    }
                    if (mMyTools.isEventWithdraw()){
                        MAPP.isEventWithdraw = true;
                        MSTRING("isEventWithdraw 撤回有消息");
                        int type = ( KCB_LIST_MSG_WITHDRAW * 4 ) | (1 << 24);           // 第1层 KCB_LIST_MSG_WITHDRAW位置
                        int time = mMyTools.getDifferentTime(type);
                        MSTRING("systemOnTimer 撤回  完成");
                        mMyTools.setDifferentRefreshed(type, time);
                    }
                }
            }
        });
    }
}
