package ltd.kcdevice;

import android.bluetooth.BluetoothAdapter;

import java.util.List;

import ltd.advskin.MRAM;
import ltd.advskin.MSKIN;
import ltd.advskin.base.BaseInput;
import ltd.advskin.base.MPAGE;
import ltd.kcdevice.base.BTickBytes;
import ltd.kcdevice.base.Kc3xType;
import ltd.kcdevice.device.KcmCtrl;
import ltd.kcdevice.device.KcmText;
import ltd.kcdevice.device.RunBle;
import ltd.kcdevice.base.ScanListener;
import ltd.kcdevice.base.BDevice;
import ltd.kcdevice.device.DevUtils;
import ltd.kcdevice.page.DeviceCenterPage;
import ltd.kcdevice.page.DeviceModifyPage;
import ltd.kcdevice.page.MicDetailAdjPage;
import ltd.kcdevice.page.DeviceSearchPage;
import ltd.kcdevice.model.Kc35hPage;
import ltd.kcdevice.page.NoiseTestPage;
import ltd.kcdevice.page.SetupPage;
import ltd.kcdevice.page.SpeakerSetupPage;
import ltd.kcdevice.page.VersionInfoPage;

import static main.MAPI.MSTRING;
import static main.MAPI.MTOAST;

public class MKCDEV {
    public final  static int DEVICE_INFO_BYTE = 1;                  // 保存 BDevice。InfoByte
    public final  static int DEVICE_BYTE_PRODUCT = 3;               // 保存 BDevice。ProductByte
    public final  static int DEVICE_BYTE_RMAUDCTR = 5;              // 保存 BDevice。RmAudCtrByte
    public final  static int DEVICE_BYTE_EQVALUE = 7;              // 保存 BDevice。EqValueByte
    public final  static int DEVICE_TIME = 9;                       // 保存 BDevice。Time
    public final  static int DEVICE_SELECT = 11;                       // 选择的电器mac
    public final  static int DEVICE_VIEW_SEQUENCE = 19;             // 记录VIEW使用的次序


    public final  static int GATTS_SERVICE_UUID_FWB = 0x000000ff;     // 选择的电器BLE的FWB通讯服务UUID
    public final  static int GATTS_CHAR_UUID_FWB = 0xff01;            // 选择的电器BLE的FWB通讯特征UUID
    public final  static int GATTS_SERVICE_UUID_SPECTRUM = 0x000000ee;    // 选择的电器BLE的频谱显示服务UUID
    public final  static int GATTS_CHAR_UUID_SPECTRUM = 0xee01;            // 选择的电器BLE的频谱显示特征UUUID

    public static BluetoothAdapter mBluetooth;
    public static long gStopTick;
    public static boolean isSetStop;
    public static ScanListener mScanListener;
    public static BluetoothAdapter.LeScanCallback mLeScanCallback;

    public static RunBle mRunBle;
    public static KcmCtrl mKcmCtrl;
    public static KcmText mKcmText;

    public static List<BTickBytes> mSendFifo;


    public static void stopDevice() {
        if (mRunBle != null) {
            mRunBle.setClose = true;
        }
        mKcmCtrl = null;
        mKcmText = null;
    }

    public static void onTimer() {

    }

    public static int getThemeStyle() {
        return MRAM.gThemeStyle;
    }

    public static void setStartUp(int fixMode, String fixIPAddr) {
        MSKIN.checkMainInit(fixMode, fixIPAddr);
    }

    public static void addTheme() {
        MSKIN.setTheme(MRAM.gThemeStyle + 1);
    }

    public static void scanBle(ScanListener scanListener) {
        DevUtils devUtils = new DevUtils();
        devUtils.searchBle(scanListener);

    }
    public static void stopScanBle(ScanListener scanListener) {
        DevUtils devUtils = new DevUtils();
        devUtils.searchBle(true, scanListener);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void startDomainCenterPage() {

    }
    public static void startDeviceCenterPage() {
        DeviceCenterPage deviceCenterPage = new DeviceCenterPage();
        deviceCenterPage.gStyle = MPAGE.PAGE_SINGLE_TASK;   // 只能一个页面存在
        deviceCenterPage.mBaseInput = new BaseInput();
        MPAGE.startBasePage(deviceCenterPage);
    }
    public static void startDeviceSearch() {
        DeviceSearchPage deviceSearchPage = new DeviceSearchPage();
        deviceSearchPage.gStyle = MPAGE.PAGE_SINGLE_TASK;   // 只能一个页面存在
        deviceSearchPage.mBaseInput = new BaseInput();
        MPAGE.startBasePage(deviceSearchPage);
    }
    public static void startKc35hPage(BDevice bDevice, String mac) {
        Kc35hPage kc35hPage = new Kc35hPage();
        kc35hPage.mBaseInput = new BaseInput(bDevice, mac);
        MPAGE.startBasePage(kc35hPage);
    }
    public static void startDeviceModifyPage() {
        DeviceModifyPage deviceModifyPage = new DeviceModifyPage();
        deviceModifyPage.gStyle = MPAGE.PAGE_SINGLE_TASK;   // 只能一个页面存在
        deviceModifyPage.mBaseInput = new BaseInput();
        MPAGE.startBasePage(deviceModifyPage);
    }
    public static void startMicDetailAdjPage() {
        MicDetailAdjPage micDetailAdjPage = new MicDetailAdjPage();
        micDetailAdjPage.gStyle = MPAGE.PAGE_SINGLE_TASK;   // 只能一个页面存在
        micDetailAdjPage.mBaseInput = new BaseInput();
        MPAGE.startBasePage(micDetailAdjPage);
    }
    public static void startNoiseTestPage() {
        NoiseTestPage noiseTestPage = new NoiseTestPage();
        noiseTestPage.gStyle = MPAGE.PAGE_SINGLE_TASK;   // 只能一个页面存在
        noiseTestPage.mBaseInput = new BaseInput();
        MPAGE.startBasePage(noiseTestPage);
    }
    public static void startSpeakerSetupPage() {
        SpeakerSetupPage speakerSetupPage = new SpeakerSetupPage();
        speakerSetupPage.gStyle = MPAGE.PAGE_SINGLE_TASK;   // 只能一个页面存在
        speakerSetupPage.mBaseInput = new BaseInput();
        MPAGE.startBasePage(speakerSetupPage);
    }
    public static void startSetupPage() {
        SetupPage setupPage = new SetupPage();
        setupPage.gStyle = MPAGE.PAGE_SINGLE_TASK;   // 只能一个页面存在
        setupPage.mBaseInput = new BaseInput();
        MPAGE.startBasePage(setupPage);
    }
    public static void startVersionInfoPage() {
        VersionInfoPage versionInfoPage = new VersionInfoPage();
        versionInfoPage.gStyle = MPAGE.PAGE_SINGLE_TASK;   // 只能一个页面存在
        versionInfoPage.mBaseInput = new BaseInput();
        MPAGE.startBasePage(versionInfoPage);
    }
    public static BDevice getPageBDevice(){
        return (mRunBle != null) ? mRunBle.mBDevice : null;
    }
    public static BDevice getRunBDevice() {
        return (mRunBle != null) ? mRunBle.mBDevice : null;
    }
    public static KcmCtrl KcmCtrl(){
        if (MKCDEV.mKcmCtrl != null){
            return MKCDEV.mKcmCtrl;
        }
        return  new KcmCtrl();
    }
    public static KcmText KcmText(){
        if (MKCDEV.mKcmText != null){
            return MKCDEV.mKcmText;
        }
        return  new KcmText();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static boolean isOnlineBle(){                                   // 查询当前是不是连接到低功耗蓝牙
        return (mRunBle != null && mRunBle.mFwbProtoc != null) ? true : false;
    }
    public static boolean isOnlineWifi(){                                   // 查询当前是不是连接到Wifi内网
        return false;
    }
    public static boolean isOnlineSvr(){                                   // 查询当前是不是通过Wifi连接到服务器
        return false;
    }
//    public static int getRmAudCtr(Kc3xType kc3xType){                       // 读取运行电器的寄存器
//        return CtrlAudio().getRmAudCtr(getRunBDevice(), kc3xType);
//    }
    public static int getRmAudCtr(BDevice bDevice, Kc3xType kc3xType){      // 读取指定电器的寄存器
        return KcmCtrl().getRmAudCtr(bDevice, kc3xType);
    }
    public static String getRmAudCtrText(Kc3xType kc3xType, int value) {      // 读取指定电器的寄存器数值对应的文字
        return KcmText().getRmAudCtrText(kc3xType, value);
    }


    public static void wrFifo(byte cmdTyp, byte[]inData, int offset, int length){     // 写入电器的FIFO
        KcmCtrl().wrFifo(cmdTyp, inData, offset, length);
    }
    public static void wrKc3xType(Kc3xType kc3xType, int vaule){               // 写入电器的寄存器
        KcmCtrl().wrKc3xType(kc3xType, vaule);
    }


    public static void initRegEqData(int index){                           // 初始化EQ数值寄存器
        if (mRunBle != null){
            mRunBle.initRegEqData(index);
        }
    }


//    public static void rdRegister(RmAudCtr rmAudCtr){                           // 读取电器的寄存器
//        CtrlAudio().rdRegister(rmAudCtr);
//    }

    //    public static void setVolumeMute(boolean up, boolean down, boolean setMute, boolean unmute) {   // KCM_VOLUME_MUTE音频静音及音量加减控制
//        CtrlAudio().setVolumeMute(up, down, setMute, unmute);
//    }
//    public static void setTestTone(boolean enable, SpkChannel spkChannel) {               // KCM_TEST_TONE噪音测试控制
//        CtrlAudio().setTestTone(enable, spkChannel);
//    }
//    public void setSpkConfig(SpkArea spkArea, SpkSetup spkSetup) {     // KCM_SPK_CONFIG喇叭设置
//        CtrlAudio().setSpkConfig(spkArea, spkSetup);
//    }
}
