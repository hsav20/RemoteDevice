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
import ltd.advskin.view.KcBottomView;
import ltd.advskin.view.KcBtnImage;
import ltd.advskin.view.KcHeaderView;
import ltd.kcdevice.R;
import ltd.kcdevice.base.BDevice;
import ltd.kcdevice.device.DevUtils;
import ltd.kcdevice.view.ViewHolder;
import main.MAPI;

import static ltd.advskin.utils.AdvBitmap.C3_FG_NORMAL;
import static ltd.advskin.utils.AdvBitmap.CROP_NONE;
import static main.MAPI.MSTRING;


public class DeviceModifyPage extends BasePage {
    private BDevice[] mBDevice;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mManager;
    private RecyclerView.Adapter mAdapter;

    private boolean isEditMode;
    private boolean isSelectAll;
    private boolean[] mEditSelect;

    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    @Override
    public void onInitView(){                       // 使用者需要继承，初始化页面控件等显示
        setLayout(R.layout.page_device_delete);

        mKcHeaderView = (KcHeaderView) findViewById(R.id.khvDeleteTitel);
        mKcHeaderView.setTitle(getString(R.string.edit_device), new KcTypeListener() {
            @Override
            public void onMessage(int type) {
                switch (type) {
                    case KcHeaderView.TYPE_CLICK_LEFT:
                        setClose();
                        break;
                    case KcHeaderView.TYPE_CLICK_RIGHR:
                        isEditMode = !isEditMode;
                        if (isEditMode){
                            mKcBottomView.setVisibility(View.VISIBLE);
                            editSelectAll(false);
                        }else{
                            isSelectAll = false;
                            mKcBottomView.setVisibility(View.GONE);
                        }
                        setListView();
                        break;
                }
            }
        });
        mKcHeaderView.setRight(C.c3i_clear, null);

        mKcBottomView = (KcBottomView) findViewById(R.id.kbtDeleteBottom);
        mKcBottomView.initView(new KcTypeListener() {
            @Override
            public void onMessage(int type){
                switch (type){
                    case 0:
                        editSelectAll(true);
                        setListView();
                        break;
                    case 2:
                        clickDelete();
                        break;
                }
            }
        });
        mKcBottomView.setVisibility(View.GONE);
        initListView();
    }
    @Override
    public void onInitData(){                       // 初始化数据，总是在初始化控件后300毫秒被调用，实现先出来页面再出来真实数据分开，防止数据多时卡
        setListView();

//        isEditMode = true;
//        editSelectAll(false);
    }
    @Override
    public void onPageUpdate(BasePage basePage){    // 更新显示页面数据，用于刷新显示 basePage返回上面的页面，可以使用getPageResult()获得返回的内容
    }
    @Override
    public void onFinish(){                         // 使用者选择性继承，用户按了后退键，不继承就直接关闭了
        setClose();
    }

    private void setListView(){
        DevUtils devUtils = new DevUtils();
        int length = devUtils.getBDeviceList().size();
        mBDevice = devUtils.getBDeviceList().toArray(new BDevice[length]);
        MLOG(String.format("DeviceDeletePage mBDevice %d %s", mBDevice.length, devUtils.getSelect()));
        mAdapter.notifyDataSetChanged();
    }

    private void editSelectAll(boolean chang){   // 选择功能
        if (chang) {
            isSelectAll = !isSelectAll;
        }
        mKcBottomView.setItem(2, 0, "删除");  // 底部的添加的文字
        boolean isSelect = false;
        if (chang && isSelectAll){
            isSelect = true;
        }
        mEditSelect = new boolean[mBDevice.length];
        for (int counter = 0; counter < mBDevice.length; counter++){
            mEditSelect[counter] = isSelect;
        }
        showSelectAll();
    }
    public void showSelectAll(){  // 全部选择显示
        isSelectAll = true;
        for (int counter = 0; counter < mBDevice.length; counter++){
            if (!mEditSelect[counter]) {   // 判断是否删除所有
                isSelectAll = false;
            }
        }
        mKcBottomView.setItem(0, isSelectAll ? C.c3i_select : C.c3i_select_not_yet, getString(R.string.all_choice));
    }
    private void clickDelete(){   // 删除功能
        showSelectAll();
        boolean deleteAll = true;
        DevUtils devUtils = new DevUtils();
        for (int counter = 0; counter < mBDevice.length; counter++){
            if (!mEditSelect[counter]){
                deleteAll = false;
                break;
            }
        }
        if (deleteAll){  // 删除所有
            devUtils.deleteAllDevice();     // 删除全部设备
        }else {
            for (int counter = 0; counter < mBDevice.length; counter++) {
                if (mEditSelect[counter]) {
                    devUtils.deleteDevice(mBDevice[counter]);       // 将设备从列表中删除
                }
            }
        }
        setListView();
        editSelectAll(false);
    }

    private void initListView(){
        View view = findViewById(R.id.rvDelete);                // 列表的布局
        mAdapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ViewHolder holder = new ViewHolder(LayoutInflater.from(MAPI.mActivity).inflate(R.layout.item_device_delete, parent, false));
                holder.mView = new View[5];
                holder.mView[0] = holder.itemView.findViewById(R.id.kbtCenterName);
                holder.mView[1] = holder.itemView.findViewById(R.id.kbtCenterMac);
                holder.mView[2] = holder.itemView.findViewById(R.id.kbtCenterRssi);
                holder.mView[3] = holder.itemView.findViewById(R.id.kbpModifyName);
                holder.mView[4] = holder.itemView.findViewById(R.id.kbiDeleteCenter);
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ViewHolder viewHolder = (ViewHolder)holder;
                BDevice bDevice = mBDevice[position];
                MSKIN.setText(bDevice.Name, viewHolder.mView[0]);
                MSKIN.setText(bDevice.Mac, viewHolder.mView[1]);
                MSKIN.setText(String.format("%s", bDevice.Rssi), viewHolder.mView[2]);

                MSKIN.setText(getString(R.string.device_name), viewHolder.mView[3]);
                viewHolder.mView[3].setTag(bDevice);
                viewHolder.mView[3].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BDevice bDevice = (BDevice) v.getTag();
                        MSKIN.startSkinInputEmoji(true, 30, getString(R.string.modify_device_name), bDevice.Name, new KcTwoListener() {
                            @Override
                            public void onMessage(Object object1, Object object2) {
                                String nickName = (String) object2;
//                                mMyManage.modifyUserNickName(nickName);       // 修改成功的必要代码
//                                InsertModifyData(wpapper.KCB_MODIFY_USER_NAME, 0, nickName);
                                MLOG(String.format("DeviceModifyPage AA %s_%s", object1, object2));
                            }
                        });
                    }
                });


                if (isEditMode && mEditSelect != null) {
                    viewHolder.mView[4].setVisibility(View.VISIBLE);
                    MSKIN.setBitmap(C3_FG_NORMAL | CROP_NONE, mEditSelect[position] ? C.c3i_select : C.c3i_select_not_yet, viewHolder.mView[4]);
                    viewHolder.mView[4].setTag(R.id.kbiDeleteCenter, viewHolder.mView[4]);
                    viewHolder.mView[4].setTag(position);
                    viewHolder.mView[4].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = (int) v.getTag();
                            mEditSelect[position] = !mEditSelect[position];
                            KcBtnImage kcBtnImage = (KcBtnImage) v.getTag(R.id.kbiDeleteCenter);
                            MSKIN.setBitmap(C3_FG_NORMAL | CROP_NONE, mEditSelect[position] ? C.c3i_select : C.c3i_select_not_yet, kcBtnImage);
                            showSelectAll();
                        }
                    });
                } else {
                    viewHolder.mView[4].setVisibility(View.GONE);
                }
            }
            @Override
            public int getItemCount() {
                int size = (mBDevice != null) ? mBDevice.length : 0;
                MSTRING(String.format("DeviceDeletePage size %s",size));
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

