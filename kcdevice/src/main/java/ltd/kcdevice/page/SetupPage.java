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
import ltd.advskin.view.KcHeaderView;
import ltd.kcdevice.MKCDEV;
import ltd.kcdevice.R;
import ltd.kcdevice.base.BDevice;
import ltd.kcdevice.device.DevUtils;
import ltd.kcdevice.view.ViewHolder;
import main.MAPI;

import static main.MAPI.MSTRING;

public class SetupPage extends BasePage {

    private BDevice mBDevice;
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
        setLayout(R.layout.page_device_setup);

        mKcHeaderView = (KcHeaderView) findViewById(R.id.khvDeviceSetup);
        mKcHeaderView.setTitle("设置", new KcTypeListener() {
            @Override
            public void onMessage(int type) {
                switch (type) {
                    case KcHeaderView.TYPE_CLICK_LEFT:
                        setClose();
                        break;
                }
            }
        });
        initListView();
        mBDevice = MKCDEV.getPageBDevice();     // 调用设备的信息

    }

    @Override
    public void onInitData(){                       // 初始化数据，总是在初始化控件后300毫秒被调用，实现先出来页面再出来真实数据分开，防止数据多时卡
//        DevUtils devUtils = new DevUtils();
//        int length = devUtils.getBDeviceList().size();
//        mBDevice = devUtils.getBDeviceList().toArray(new BDevice[length]);
//        MLOG(String.format("DeviceCenterPage mBDevice %d %s", mBDevice.length, devUtils.getSelect()));
        setListView();
    }
    @Override
    public void onPageUpdate(BasePage basePage){    // 更新显示页面数据，用于刷新显示 basePage返回上面的页面，可以使用getPageResult()获得返回的内容
    }
    @Override
    public void onFinish(){                         // 使用者选择性继承，用户按了后退键，不继承就直接关闭了
        setClose();
    }
    private void setListView(){
        mAdapter.notifyDataSetChanged();
    }
    private void initListView(){
        View view = findViewById(R.id.rvDeviceSetup);                // 列表的布局
        mAdapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ViewHolder holder = new ViewHolder(LayoutInflater.from(MAPI.mActivity).inflate(R.layout.item_device_setup, parent, false));
                holder.mView = new View[3];
                holder.mView[0] = holder.itemView.findViewById(R.id.kbtDeviceSetup);
                holder.mView[1] = holder.itemView.findViewById(R.id.kbtDeviceSetupDetail);
                holder.mView[2] = holder.itemView.findViewById(R.id.kbiDeviceSetup);
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ViewHolder viewHolder = (ViewHolder)holder;
                String[] gpLocal_1 = MSKIN.getStringArray(R.array.device_setup);
                MSKIN.setText(gpLocal_1[position], viewHolder.mView[0]);
                MSKIN.setBitmap(C.c3i_chose_arrow, viewHolder.mView[2]);
//                MSKIN.setText("AAA", viewHolder.mView[1]);
                switch (position){
                    case 0:
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                BDevice bDevice = (BDevice) v.getTag();
                                MSKIN.startSkinInputEmoji(true, 30, getString(R.string.modify_device_name), mBDevice.Name, new KcTwoListener() {
                                    @Override
                                    public void onMessage(Object object1, Object object2) {
                                        String nickName = (String) object2;
                                        MLOG(String.format("DeviceModifyPage AA %s_%s", object1, object2));
                                    }
                                });
                            }
                        });
                        break;
                    case 1:
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int position = (int) v.getTag();
                                MLOG(String.format("SetupPage BBB %s", position));
                            }
                        });
                        break;
                    case 2:
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int position = (int) v.getTag();
                                MLOG(String.format("SetupPage CCC %s", position));
                            }
                        });
                        break;
                    case 3:
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                int position = (int) v.getTag();
                                MKCDEV.startVersionInfoPage();
                            }
                        });
                        break;
                }
            }
            @Override
            public int getItemCount() {
//                int size = (mBDevice != null) ? mBDevice.length : 0;
                int size = 4;
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

