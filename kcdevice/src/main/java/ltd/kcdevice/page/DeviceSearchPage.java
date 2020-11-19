package ltd.kcdevice.page;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ltd.advskin.MSKIN;
import ltd.advskin.base.BasePage;
import ltd.advskin.base.KcListener;
import ltd.advskin.base.KcTypeListener;
import ltd.advskin.permission.PermissionsChecker;
import ltd.advskin.view.KcHeaderView;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.R;
import ltd.kcdevice.base.ScanListener;
import ltd.kcdevice.base.BDevice;
import ltd.kcdevice.device.DevUtils;
import ltd.kcdevice.view.ViewHolder;
import main.MAPI;

import static main.MAPI.MSTRING;
import static main.MAPI.MTOAST;


public class DeviceSearchPage extends BasePage {
    private List<BDevice> mBDevice;
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
        setLayout(R.layout.page_device_search);
        mKcHeaderView = (KcHeaderView) findViewById(R.id.khvSearch);
        mKcHeaderView.setTitle(getString(R.string.search_device), new KcTypeListener() {
            @Override
            public void onMessage(int type) {
                switch (type) {
                    case KcHeaderView.TYPE_CLICK_LEFT:
                        closePage();
                        break;
                }
            }
        });
        initListView();
    }
    @Override
    public void onInitData(){                       // 初始化数据，总是在初始化控件后300毫秒被调用，实现先出来页面再出来真实数据分开，防止数据多时卡
        getBluetoothPermissions();
    }
    @Override
    public void onPageUpdate(BasePage basePage){    // 更新显示页面数据，用于刷新显示 basePage返回上面的页面，可以使用getPageResult()获得返回的内容
    }
    @Override
    public void onFinish(){                         // 使用者选择性继承，用户按了后退键，不继承就直接关闭了
        closePage();
    }
    public void closePage() {
        MKCDEV.stopScanBle(null);
        setClose();
    }

    private void getBluetoothPermissions(){
        PermissionsChecker checker = new PermissionsChecker();
        String[] PERMISSIONS = checker.getFineLocation();   // 当前手机扫描蓝牙需要打开定位功能
        if (checker.lacksPermissions(PERMISSIONS)) {
            MSKIN.startPermissionsActivity(PERMISSIONS, new KcListener() {
                @Override
                public void onMessage(Object object) {
                    if (object == null){
                        startBluetooth();
                    }else {
                        MTOAST("");
                    }
                }
            });

        }else {
            startBluetooth();
        }
    }
    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) MAPI.mActivity.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null){
            return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        }
        return false;
    }


    private void startBluetooth(){
        MLOG("开始扫描蓝牙电器");
        MKCDEV.scanBle(new ScanListener(){
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//                MLOG(String.format("开始扫描蓝牙电器 AA %s_%s_%s_%s", device,  rssi, scanRecord, mBDevice));
                DevUtils devUtils = new DevUtils();
                if (mBDevice == null){
                    mBDevice = new ArrayList<>();
                }
                int length = mBDevice.size();
                BDevice bDevice = devUtils.addDeviceList(device.getAddress(), scanRecord, mBDevice);
                bDevice.Rssi = rssi;
//                MLOG(String.format("开始扫描蓝牙电器 BB %s_%s", length,  mBDevice.size()));
                if (length != mBDevice.size()){             // 新加入的
                    setListView();
//                    MLOG(String.format("DeviceSearchPage addDeviceList A %s %s %s %s", device.getAddress(), bDevice.Name, rssi, mBDevice.size()));
//                    // 测试用！！！自动加入数据库
//                    if (!MAPI.isEmpty(bDevice.Name)){
//                        devUtils.saveDevice(bDevice);
//                        devUtils.setSelect(bDevice.Mac);
//                        MLOG(String.format("addDeviceList B %s", bDevice.Mac));
//                        setListView();
//                    }
                }
            }
        });
    }


    private void setListView(){
        MLOG(String.format("setListView AA %s", (mBDevice != null) ? mBDevice.size() : 0));
//        mRecyclerView.setHasFixedSize(true);
        mAdapter.notifyDataSetChanged();
        MLOG(String.format("setListView BB"));
    }

    private void initListView(){
        View view = findViewById(R.id.rvSearch);                // 列表的布局
        mAdapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ViewHolder holder = new ViewHolder(LayoutInflater.from(MAPI.mActivity).inflate(R.layout.item_device_search, parent, false));
                holder.mView = new View[4];
                holder.mView[0] = holder.itemView.findViewById(R.id.kbtName);
                holder.mView[1] = holder.itemView.findViewById(R.id.kbtMac);
                holder.mView[2] = holder.itemView.findViewById(R.id.kbtRssi);
                holder.mView[3] = holder.itemView.findViewById(R.id.kbpConnect);
                return holder;
            }
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ViewHolder viewHolder = (ViewHolder)holder;
                BDevice bDevice = mBDevice.get(position);
                MSKIN.setText(bDevice.Name, viewHolder.mView[0]);
                MSKIN.setText(bDevice.Mac, viewHolder.mView[1]);
                MSKIN.setText(String.format("%s", bDevice.Rssi), viewHolder.mView[2]);

                DevUtils devUtils = new DevUtils();
                if (devUtils.getDevice(bDevice.Mac) == true){       // 判断设备是否已经加入列表
                    MSKIN.setText("已加入", viewHolder.mView[3]);
                    viewHolder.mView[3].setTag(bDevice);
                    viewHolder.mView[3].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DevUtils devUtils = new DevUtils();
                            BDevice bDevice = (BDevice) v.getTag();
                            devUtils.deleteDevice(bDevice);       // 将设备从列表中删除
                            setListView();
                            MTOAST("已经删除设备");
                        }
                    });
                }else {
                    MSKIN.setText("加入", viewHolder.mView[3]);
                    viewHolder.mView[3].setTag(bDevice);
                    viewHolder.mView[3].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DevUtils devUtils = new DevUtils();
                            BDevice bDevice = (BDevice) v.getTag();
                            devUtils.saveDevice(bDevice);       // 将设备加入到列表中
                            setListView();
                            MTOAST(String.format("已成功加入设备"));
                        }
                    });
                }
                MLOG(String.format("DeviceSearchPage AA (%s)_%s_%s_%s", position, bDevice.Name, bDevice.Mac, bDevice.Rssi));
            }
            @Override
            public int getItemCount() {
                int size = (mBDevice != null) ? mBDevice.size() : 0;
                MLOG(String.format("DeviceSearchPage size %s_%s",size, mBDevice));
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

