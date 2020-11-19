package ltd.kcdevice.page;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ltd.advskin.C;
import ltd.advskin.MSKIN;
import ltd.advskin.base.BasePage;
import ltd.advskin.base.KcTwoListener;
import ltd.advskin.base.KcTypeListener;
import ltd.advskin.view.KcBtnText;
import ltd.advskin.view.KcHeaderView;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.R;
import ltd.kcdevice.base.BDevice;
import ltd.kcdevice.device.ProductInfo;
import main.MAPI;
import static main.MAPI.MSTRING;

public class VersionInfoPage extends BasePage {
    private BDevice mBDevice;
    private KcBtnText kbtVersionInfoA, kbtVersionInfoB, kbtVersionInfoC;

    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    @Override
    public void onInitView(){                       // 使用者需要继承，初始化页面控件等显示
        setLayout(R.layout.page_version_info);
        mKcHeaderView = (KcHeaderView) findViewById(R.id.khvVersionInfo);
        mKcHeaderView.setTitle("版本信息", new KcTypeListener() {
            @Override
            public void onMessage(int type) {
                switch (type) {
                    case KcHeaderView.TYPE_CLICK_LEFT:
                        setClose();
                        break;
                }
            }
        });
        mBDevice = MKCDEV.getPageBDevice();     // 调用设备的信息
        kbtVersionInfoA = (KcBtnText) findViewById(R.id.kbtVersionInfoA);
        kbtVersionInfoB = (KcBtnText) findViewById(R.id.kbtVersionInfoB);
        kbtVersionInfoC = (KcBtnText) findViewById(R.id.kbtVersionInfoC);
    }

    @Override
    public void onInitData(){                       // 初始化数据，总是在初始化控件后300毫秒被调用，实现先出来页面再出来真实数据分开，防止数据多时卡
        showInfo();
    }
    @Override
    public void onPageUpdate(BasePage basePage){    // 更新显示页面数据，用于刷新显示 basePage返回上面的页面，可以使用getPageResult()获得返回的内容
    }
    private void showInfo(){

        ProductInfo productInfo = new ProductInfo();
        String t1 = productInfo.getDateTime(mBDevice, ProductInfo.PRODUCT_DATE_VER + 0);
        String t2 = productInfo.getVersion(mBDevice, ProductInfo.PRODUCT_DATE_VER + 0);
        String t3 = productInfo.getDateTime(mBDevice, ProductInfo.PRODUCT_DATE_VER + 8);
        String t4 = productInfo.getVersion(mBDevice, ProductInfo.PRODUCT_DATE_VER + 8);
        String t5 = productInfo.getDateTime(mBDevice, ProductInfo.PRODUCT_DATE_VER + 16);
        String t6 = productInfo.getVersion(mBDevice, ProductInfo.PRODUCT_DATE_VER + 16);
        MLOG(String.format("VersionInfoPage 固件 Kc35h_A_V%s(%s) Kc35h_B_V%s(%s) WfBtKc3x_V%s(%s) ", t1, t2, t3, t4, t5, t6));

//        if (productInfo.isKc35h(mBDevice)) {
//            MLOG(String.format("VersionInfoPage 固件 Kc35h_A_V%s(%s) Kc35h_B_V%s(%s) WfBtKc3x_V%s(%s) ", t1, t2, t3, t4, t5, t6));
//        }

        String gsLocal_A = String.format("固件 Kc35h_A_V%s(%s)", t1, t2);
        String gsLocal_B = String.format("固件 Kc35h_B_V%s(%s)",t3, t4);
        String gsLocal_W = String.format("固件 WfBtKc3x_V%s(%s)",t5, t6);

//        MLOG(String.format("VersionInfoPage AA %s_%s_%s_%s_%s_%s", mBDevice.Name, mBDevice.Mac, mBDevice.InfoByte, mBDevice.EqValueByte, mBDevice.ProductByte, mBDevice.RmAudCtrByte));
        MLOG(String.format("VersionInfoPage BB %s_%s_%s", gsLocal_A, gsLocal_B, gsLocal_W));
        MSKIN.setText(gsLocal_A, kbtVersionInfoA);
        MSKIN.setText(gsLocal_B, kbtVersionInfoB);
        MSKIN.setText(gsLocal_W, kbtVersionInfoC);
    }
    @Override
    public void onFinish(){                         // 使用者选择性继承，用户按了后退键，不继承就直接关闭了
        setClose();
    }

}

