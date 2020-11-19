package ltd.kcdevice.base;

public enum InputSrc {
    IN_ANALOG(0x00), IN_RX1(0x10), IN_RX2(0x11), IN_RX3(0x12),
    HDMI_1(0x20), HDMI_2(0x21), HDMI_3(0x22),
    SD_TF(0x30), UDISK(0x31), USB_PC(0x32), BLUETOOTH(0x33);
    public byte Value;
    InputSrc(int value) {
        this.Value = (byte) value;
    }
};