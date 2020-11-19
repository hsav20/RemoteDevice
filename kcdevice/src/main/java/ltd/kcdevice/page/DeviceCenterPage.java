package ltd.kcdevice.page;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kcbsdk.wpapper;

import ltd.advskin.C;
import ltd.advskin.MSKIN;
import ltd.advskin.base.BasePage;
import ltd.advskin.base.KcTwoListener;
import ltd.advskin.base.KcTypeListener;
import ltd.advskin.view.KcBottomView;
import ltd.advskin.view.KcHeaderView;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.R;
import ltd.kcdevice.base.ScanListener;
import ltd.kcdevice.base.BDevice;
import ltd.kcdevice.device.DevUtils;
import ltd.kcdevice.view.ViewHolder;
import main.MAPI;

import static ltd.advskin.MRAM.mMyManage;
import static main.MAPI.MSTRING;


public class DeviceCenterPage extends BasePage {
    private BDevice[] mBDevice;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mManager;
    private RecyclerView.Adapter mAdapter;

    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }
    @Override
    public void onInitView(){                       // 使用者需要继承，初始化页面控件等显示
        setLayout(R.layout.page_device_center);

        mKcHeaderView = (KcHeaderView) findViewById(R.id.khvCenter);
        mKcHeaderView.setTitle(getString(R.string.device_center), new KcTypeListener() {
            @Override
            public void onMessage(int type) {
                switch (type) {
                    case KcHeaderView.TYPE_CLICK_LEFT:
                        setClose();
                        break;
                }
            }
        });

        mKcBottomView = (KcBottomView) findViewById(R.id.kbvCenter);
        mKcBottomView.initView(new KcTypeListener() {
            @Override
            public void onMessage(int type){
                switch (type){
                    case 1:
                        MKCDEV.mBluetooth = null;
                        MKCDEV.startDeviceSearch();
                        break;
                    case 2:
                        MKCDEV.startDeviceModifyPage();
                        break;
                }
            }
        });
        mKcBottomView.setItem(1, 0, getString(R.string.search_device));  // 底部的添加的文字
        mKcBottomView.setItem(2, 0, getString(R.string.edit_device));  // 底部的添加的文字
        initListView();
    }
    @Override
    public void onInitData(){                       // 初始化数据，总是在初始化控件后300毫秒被调用，实现先出来页面再出来真实数据分开，防止数据多时卡
        DevUtils devUtils = new DevUtils();
        int length = devUtils.getBDeviceList().size();
        mBDevice = devUtils.getBDeviceList().toArray(new BDevice[length]);
        MLOG(String.format("DeviceCenterPage mBDevice %d %s", mBDevice.length, devUtils.getSelect()));
        setListView();

        MKCDEV.scanBle(new ScanListener(){
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                for (int index = 0; index < mBDevice.length; index++) {
                    BDevice bDevice = mBDevice[index];
                    if (bDevice.Mac.equals(device.getAddress())){
                        if (bDevice.Bluetooth == null){
                            bDevice.Bluetooth = device;
                            MLOG(String.format("DeviceCenterPage AA %s %s", bDevice.Mac, bDevice.Name));
                        }
                        bDevice.Rssi = rssi;
                    }
                }
            }
        });
    }
    @Override
    public void onPageUpdate(BasePage basePage){    // 更新显示页面数据，用于刷新显示 basePage返回上面的页面，可以使用getPageResult()获得返回的内容
        onInitData();
    }
    @Override
    public void onFinish(){                         // 使用者选择性继承，用户按了后退键，不继承就直接关闭了
        setClose();
    }

    private void setListView(){
        mAdapter.notifyDataSetChanged();
    }

    private void initListView(){
        View view = findViewById(R.id.rvCenter);                // 列表的布局
        mAdapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ViewHolder holder = new ViewHolder(LayoutInflater.from(MAPI.mActivity).inflate(R.layout.item_device_center, parent, false));
                holder.mView = new View[3];
                holder.mView[0] = holder.itemView.findViewById(R.id.kbtCenterName);
                holder.mView[1] = holder.itemView.findViewById(R.id.kbtCenterMac);
                holder.mView[2] = holder.itemView.findViewById(R.id.kbtCenterRssi);
//                holder.mView[3] = holder.itemView.findViewById(R.id.kbpCenter);
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ViewHolder viewHolder = (ViewHolder)holder;
                BDevice bDevice = mBDevice[position];
                MSKIN.setText(bDevice.Name, viewHolder.mView[0]);
                MSKIN.setText(bDevice.Mac, viewHolder.mView[1]);
                MSKIN.setText(String.format("%s", bDevice.Rssi), viewHolder.mView[2]);

                viewHolder.itemView.setTag(bDevice);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BDevice bDevice = (BDevice) v.getTag();
                        MKCDEV.startKc35hPage(bDevice, bDevice.Mac);
                        MLOG(String.format("DeviceCenterPage BBBB (%s)", bDevice.Mac));
                    }
                });
            }
            @Override
            public int getItemCount() {
                int size = (mBDevice != null) ? mBDevice.length : 0;
                return size;
            }
            @Override
            public int getItemViewType(int position) {
                return position;                                    // 可以根据行数据决定viewType
            }
        };
        mManager = new LinearLayoutManager(MAPI.mContext);
        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mAdapter);
    }

}

