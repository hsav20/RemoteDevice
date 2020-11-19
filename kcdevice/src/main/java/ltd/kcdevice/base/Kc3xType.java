package ltd.kcdevice.base;

public enum Kc3xType {
    KCM_POWER_ON((byte)0x05),				// 用户主机上电
    KCM_SRC_FORMAT((byte)0x06),				// 数码信号输入格式及通道信息指示
    KCM_BPS_RATE((byte)0x07),				// 采样频率及码流率指示
    KCM_VOLUME_MUTE((byte)0x08),				// 音频静音及音量加减控制
    KCM_TEST_TONE((byte)0x09),				// 噪音测试
    KCM_WIFI_STATUS((byte)0x0d),				// WIFI状态指示
    KCM_PLAY_STATUS((byte)0x0e),				// 多媒体文件播放状态
    KCM_PLAY_OPERATE((byte)0x0f),			// 多媒体文件播放控制
    KCM_PLAY_INDEX((byte)0x10),				// 控制多媒体文件播放编号，16位寄存器
    KCM_PLAY_TIME((byte)0x12),				// 读取多媒体文件正在播放的时间，16位寄存器
    KCM_SRC_VALID((byte)0x1c),				// 有效的音源输入改变，16位寄存器
    KCM_SRC_DETECT((byte)0x1f),				// 检测所有有效的音源一次

    KCM_INPUT_SOURCE((byte)0x20),			// 输入音源选择（带上电记忆）
    KCM_INPUT_VIDEO((byte)0x21),				// 输入视频源选择（带上电记忆）
    KCM_DYN_COMPRES((byte)0x23),				// 杜比数码动态压缩（带上电记忆）
    KCM_SPK_CONFIG((byte)0x24),				// 喇叭设置（带上电记忆）
    KCM_LPF_FREQ((byte)0x25),				// 超低音通道LPF低通滤波器频率（带上电记忆）
    KCM_HPF_FREQ((byte)0x26),				// 主声道小喇叭HPF高通滤波器频率（带上电记忆）
    KCM_LIP_SYNC_SET((byte)0x28),			// 齿音同步延迟时间，修正对画面与声音不同步（带上电记忆）
    KCM_LIP_SYNC_MAX((byte)0x29),			// 齿音同步最大的延迟时间（带上电记忆）
    KCM_LISTEN_MODE((byte)0x2b),				// 聆听模式选择（带上电记忆）
    KCM_EQ_SELECT((byte)0x2c),				// 多路均衡音效处理选择（带上电记忆）
    KCM_VOLUME_MAX((byte)0x2e),				// 设置音量最大值（带上电记忆）
    KCM_VOLUME_CTRL((byte)0x2f),				// 音量值设置（带上电记忆）
    KCM_FL_TRIM((byte)0x30),					// 前置左声道微调 B4=1为减dB B3-B0为dB数值（带上电记忆）
    KCM_FR_TRIM((byte)0x31),					// 前置右声道微调 B4=1为减dB B3-B0为dB数值（带上电记忆）
    KCM_CE_TRIM((byte)0x32),					// 中置声道微调	  B4=1为减dB B3-B0为dB数值（带上电记忆）
    KCM_SW_TRIM((byte)0x33),					// 超低音声道微调 B4=1为减dB B3-B0为dB数值（带上电记忆）
    KCM_SL_TRIM((byte)0x34),					// 环绕左声道微调 B4=1为减dB B3-B0为dB数值（带上电记忆）
    KCM_SR_TRIM((byte)0x35),					// 环绕右声道微调 B4=1为减dB B3-B0为dB数值（带上电记忆）
    KCM_BL_TRIM((byte)0x36),					// 后置左声道微调 B4=1为减dB B3-B0为dB数值（带上电记忆）
    KCM_BR_TRIM((byte)0x37),					// 后置右声道微调 B4=1为减dB B3-B0为dB数值（带上电记忆）
    KCM_MIC_DELAY((byte)0x38),				// 话筒延迟时间，每步20毫秒
    KCM_MIC_VOLUME((byte)0x39),				// 话筒1及话筒2音量比例
    KCM_MIC_ECHO_EQ((byte)0x3a),				// 话筒回声比例及话筒多段EQ均衡音效处理选择
    KCM_MIC_REPEAT((byte)0x3b),				// 话筒重复及直达声比例
    KCM_MIC_REVERB((byte)0x3c),				// 话筒混响1及混响2比例
    KCM_MIC_WHISTLE((byte)0x3d),				// 话筒啸叫声音反馈模式

    RAC_BYTE_REG_START((byte)0x06),				// 单字节寄存器开始索引 KCM_SRC_FORMAT = 0x06,
    RAC_BYTE_REG_END((byte)0x40),				// 单字节寄存器结束索引

    RAC_SET_DELAY_TIME((byte)0x40),								// KCM_DELAY_TIME设置所有声道的延迟时间 共占用8字节
    RAC_DELAY_TIME_MAX((byte)0x48),								// KCM_MAX_DELAY读取所有声道最大可用的延迟时 共占用8字节

    RAC_RD_SD_QTY((byte)0x50),				// 读取多媒体文件正在播放的时间，共2字节单位秒
    RAC_RD_UDISK_QTY((byte)0x52),			// 读取多媒体文件正在播放的时间，共2字节单位秒
    RAC_RD_FILE_TIME((byte)0x54),			// 读取多媒体文件正在播放的总时间，共2字节单位秒


//    KCM_EXTR_MEMORY((byte)0x40),				// 扩展给用户主机的掉电记忆空间，0x40-0x7f共64字节（带上电记忆）
//
//    KCM_MAX_DELAY((byte)0x86),				// 读取所有声道最大可用的延迟时间
//    KCM_DELAY_TIME((byte)0x87),				// 设置所有声道的延迟
//    KCM_PROGUCE_SIGNAL((byte)0x88),			// 模块内部产生的信号配置
//    KCM_EQ_SETUP((byte)0x8b),				// 多段EQ均衡音效处理设置
//    KCM_EQ_VALUE((byte)0x8c),				// 多段EQ均衡音效处理数值
//    KCM_MIC_EQ_VALUE((byte)0x8d);			// 话筒多段EQ均衡音效处理数值


    RAC_PRODUCT_MAX((byte)0xe0),                            // PRODUCT_INFO
    RAC_RMAUDCTR_MAX((byte)0xc0),
    RAC_EQVALUE_INDEX_MAX((byte)5),
    RAC_EQVALUE_DATA_MAX((byte)32);

    public final static byte KCM_MODEL_32C = 0x31;				// 模块型号KC32C
    public final static byte KCM_MODEL_35H = 0x53;				// 模块型号KC35H
    public final static byte KCM_MODEL_36H = 0x56;				// 模块型号KC36H


    public int Value;
    Kc3xType(byte value) {
        this.Value = value&0xff;
    }





}
