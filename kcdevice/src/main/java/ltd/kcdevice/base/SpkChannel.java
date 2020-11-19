package ltd.kcdevice.base;

// 前置左声道  前置右声道 中置声道 超低音声道 环绕左声道 环绕右声道 后置左声道 后置右声道
public enum SpkChannel {
    FL(0), FR(1), CE(2), SW(3), SL(4), SR(5), BL(6), BR(7);
    public byte Value;
    SpkChannel(int value) {
        this.Value = (byte) value;
    }
}
