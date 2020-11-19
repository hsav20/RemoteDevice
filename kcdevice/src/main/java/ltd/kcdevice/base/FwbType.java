package ltd.kcdevice.base;

public class FwbType {
    public final static int FWB_MAX_DATA = (16 + 0x1000);			// 传输包最大有效数据
    public final static byte FWB_SYNC_0 = (byte)0xf5;			    // FWB传输包同步头0
    public final static byte FWB_SYNC_1 = (byte)0x7b;			    // FWB传输包同步头1
    public final static byte FWB_ACK_FLAG = (byte)0x80;			    // 应答标志

    public final static byte FWB_CMD_NONE = (byte)0x00;	 			// 不支持的指令类型

    public final static byte FFWB_AUD_WFBT_VERSION = 0x30;							// 获取WFBT的日期版本信息
    public final static byte FWB_AUD_WFBT_TICK = 0x31;								// 同步TICK
    public final static byte FWB_AUD_PUSH_LOG = 0x32;								// 推送LOG到APP
    public final static byte FWB_AUD_PUSH_PRODUCT = 0x35;							// 读取/推送产品及网络设置寄存器
    public final static byte FWB_AUD_PUSH_RMAUDCTR = 0x36;							// 读取/推送音频控制寄存器
    public final static byte FWB_AUD_PUSH_EQ_VALUE = 0x37;							// 读取/推送EQ数值寄存器
    public final static byte FWB_AUD_WR_PRODUCT = 0x38;								// 写入/更改产品及网络设置寄存器
    public final static byte FWB_AUD_WR_RMAUDCTR = 0x39;							// 写入/更改音频控制寄存器
    public final static byte FWB_AUD_WR_EQ_VALUE = 0x3a;							// 写入/更改EQ数值寄存器



    public final static byte CODE_NONE = 0x00;										// 不支持的字符集
    public final static byte CODE_UNICODE = 0x01;									// 字符集为UNICODE
    public final static byte CODE_UTF_8 = 0x02;										// 字符集为UTF-8
    public final static byte CODE_GB2312 = 0x03;										// 字符集为GB2312（GBK）
    public final static byte CODE_BIG5 = 0x04;										// 字符集为BIG5

}
