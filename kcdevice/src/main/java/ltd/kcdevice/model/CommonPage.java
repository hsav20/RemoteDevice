package ltd.kcdevice.model;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ltd.advskin.base.BasePage;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.base.BDevice;
import ltd.kcdevice.base.FwbType;
import ltd.kcdevice.base.NotifyListener;
import ltd.kcdevice.device.DevUtils;
import ltd.kcdevice.device.RunBle;
import main.MAPI;

import static main.MAPI.MERROR;
import static main.MAPI.MSTRING;

public class CommonPage extends BasePage {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mManager;
    public RecyclerView.Adapter mAdapter;

    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    @Override
    public void onFinish(){                         // 使用者选择性继承，用户按了后退键，不继承就直接关闭了
        setNext();
    }
    public void setNext() {
        MKCDEV.stopScanBle(null);
        setClose();
    }

    public void startDevice(BDevice bDevice, String mac){
        DevUtils devUtils = new DevUtils();
        if (bDevice == null){
            if (!MAPI.isEmpty(mac)) {
                bDevice = devUtils.getBDevice(mac);
            }
        }
        if (bDevice == null){
            MERROR(String.format("CommonPage startDevice Error %s %s", bDevice, mac));
            return;
        }
        MLOG(String.format("CommonPage bDevice.Mac %s ", bDevice.Mac));
        MKCDEV.mRunBle = new RunBle();
        MKCDEV.mRunBle.setStart(bDevice, new NotifyListener() {
            @Override
            public void onMessage(byte cmdTyp, Object object) {
                if (object instanceof byte[]) {
                    byte[] inData = (byte[])object;
                    if (inData.length > (8+3)){
                        switch (cmdTyp){
                            case FwbType.FWB_AUD_PUSH_RMAUDCTR:
                            case FwbType.FWB_AUD_PUSH_EQ_VALUE:
                            case FwbType.FWB_AUD_PUSH_PRODUCT:
                                MLOG(String.format("pushRmAudCtr AA %d ", MAPI.BUFF8_WORD(6, inData)));
                                MKCDEV.KcmCtrl().updateByte(cmdTyp, inData, MAPI.BUFF8_WORD(6, inData));
                                MLOG(String.format("pushRmAudCtr BB %d ", MAPI.BUFF8_WORD(6, inData)));
                                showInfo();              // 需要在真实的页面重构 @Override showInfo()
                                MLOG(String.format("pushRmAudCtr CC %d ", MAPI.BUFF8_WORD(6, inData)));
                                break;
                        }
                    }else {
                        MLOG(String.format("CommonPage cmdTyp, data.length, data.length %02x %02x ", cmdTyp, inData.length, inData.length));
                    }
                }
            }
        });
    }
    public void showInfo(){                                 // 需要在真实的页面重构 @Override showInfo()
    }
    public void initListView(View view){
        mManager = new LinearLayoutManager(MAPI.mContext);
        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mAdapter);
//        MLOG("CommonPage initListView ");
    }

}

//
//    public void startBluetooth(KcListener kcListener) {
//        mKcListener = kcListener;
//        MLOG("CommonPage mBDevice "+ mBDevice);
//        MKCDEV.mRunBle = new RunBle();
//        MKCDEV.mRunBle.setStart(mBDevice, gMacAddr, new NotifyListener() {
//            @Override
//            public void onMessage(byte cmdTyp, Object object) {
//                if (object instanceof byte[]) {
//                    byte[] inData = (byte[])object;
//                    if (inData.length > (8+3)){
//                        switch (cmdTyp){
//                        case FwbType.FWB_AUD_PUSH_RMAUDCTR:
//                        case FwbType.FWB_AUD_PUSH_EQ_VALUE:
//                        case FwbType.FWB_AUD_PUSH_PRODUCT:
//                            MKCDEV.CtrlAudio().updateByte(cmdTyp, inData, MAPI.BUFF8_WORD(6, inData));
//                            showInfo();
////                            MLOG(String.format("pushRmAudCtr %d ", MAPI.BUFF8_WORD(6, inData)));
//                            break;
//                        }
//                    }else {
//                        MLOG(String.format("CommonPage cmdTyp, data.length, data.length %02x %02x ", cmdTyp, inData.length, inData.length));
//                    }
//                }
//            }
//        });
//    }