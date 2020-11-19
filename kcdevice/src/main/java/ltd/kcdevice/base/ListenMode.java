package ltd.kcdevice.base;

public enum ListenMode {
    HIFI_2CH(0x00), SW_2CH(0x01), SRC_FULL(0x10), MODE1_FULL(0x20), MODE2_FULL(0x21), EFFECT_FULL(0x30);
    public byte Value;
    ListenMode(int value) {
        this.Value = (byte) value;
    }
};