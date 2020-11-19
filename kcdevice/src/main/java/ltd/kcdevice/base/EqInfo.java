package ltd.kcdevice.base;

public enum EqInfo {
    SETUP0(0x00), SETUP1(0x01), SETUP2(0x02), SETUP3(0x03),
    VALUE01A(0x04), VALUE01B(0x05), VALUE01C(0x06),
    VALUE23A(0x07), VALUE23B(0x08), VALUE23C(0x09),
    VALUE45A(0x0a), VALUE45B(0x0b), VALUE45C(0x0c),
    VALUE67A(0x0d), VALUE67B(0x0e), VALUE67C(0x0f);

    public byte Value;
    EqInfo(int value) {
        this.Value = (byte) value;
    }
}
