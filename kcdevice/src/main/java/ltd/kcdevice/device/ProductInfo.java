package ltd.kcdevice.device;

import java.io.UnsupportedEncodingException;

import ltd.kcdevice.base.BDevice;
import ltd.kcdevice.base.FwbType;
import ltd.kcdevice.base.Kc3xType;
import main.MAPI;

import static main.MAPI.MSTRING;

public class ProductInfo {

    public final  static int PRODUCT_HOST_MAC				=0;					// [0-5]主机MAC
    public final  static int PRODUCT_HOST_IP					=6;					// [6-9]主机IP
    public final  static int PRODUCT_NET_CARD				=10;					// [10]主机网卡
    public final  static int PRODUCT_INTRANET				=11;					// [11]内网
    public final  static int PRODUCT_TURN_IP					=12;					// [12-15]设备实际连接的外网IP地址
    public final  static int PRODUCT_RESERVED				=16;					// [16-23]保留为0

    public final  static int PRODUCT_MAC_ADDR				=24;					// [24-29]从机MAC
    public final  static int PRODUCT_IP_ADDR					=30;					// [30-33]从机IP
    public final  static int PRODUCT_IP_MASK					=34;					// [34-37]掩码
    public final  static int PRODUCT_GATEWAY					=38;					// [38-41]网关
    public final  static int PRODUCT_IP_DNS					=42;					// [42-45]DNS
    public final  static int PRODUCT_UDP_PORT				=46;					// [46-47]调试端口
    public final  static int PRODUCT_DHCP_ENA				=48;					// [48]DHCP
    public final  static int PRODUCT_DDNS_FLAG				=49;					// [49]域名简称
    public final  static int PRODUCT_DDNS_NAME				=50;					// [50-58]域名
    public final  static int PRODUCT_DDNS_PSW				=59;					// [59-67]解析密码
    public final  static int PRODUCT_WIFI_TX_ENA				=68;					// [68]发射WIFI
    public final  static int PRODUCT_MAIN_TYPE				=69;					// [69]产品类型
    public final  static int PRODUCT_SUB_TYPE				=70;					// [70]产品子类
    public final  static int PRODUCT_KCM_MODEL				=71;					// [71]KCM模块型号
    public final  static int PRODUCT_DATE_VER               =72;					// [72-95]固件日期版本

    public final  static int PRODUCT_CHAR_CODE				=96;					// [101]当前WIFI文字长度
    public final  static int PRODUCT_BRAND_SIZE				=97;					// [96]字符集
    public final  static int PRODUCT_MODEL_SIZE				=98;					// [97]牌子文字长度
    public final  static int PRODUCT_NICK_NAME				=99;					// [98]型号文字长度
    public final  static int PRODUCT_TX_WIFI_NAME			=100;					// [99]昵称文字长度
    public final  static int PRODUCT_TRUE_WIFI				=101;					// [100]发射WIFI名文字长度
    public final  static int PRODUCT_TEXT_DATA				=102;					// [102-223]所有文字实际内码


    public final  static int PRODUCT_INFO_SAVE				=24;					// PRODUCT_INFO结构需要保存的字节开始的位置
    public final  static int PRODUCT_INFO_TEXT				=96;					// PRODUCT_INFO结构需要保存的文字开始的位置
    public final  static int PRODUCT_TEXT_SIZE				=0x7a;				// PRODUCT_TEXT结构所有文字最大字节数
    public final  static int PRODUCT_INFO_STRUCT				=0xe0;				// PRODUCT_INFO结构需要最大的字节数
// public final  static int RAC_PRODUCT_MAX 				0xe0				// PRODUCT_INFO_STRUCT

    public String getTextOffset(BDevice bDevice, int offset) {
        int length;
        int start = 0;
        if (offset > PRODUCT_BRAND_SIZE) {
            length = PRODUCT_BRAND_SIZE + (offset - PRODUCT_BRAND_SIZE);
            for (int counter = PRODUCT_BRAND_SIZE; counter < length; counter++) {
                start += bDevice.ProductByte[counter] & 0x7f;
            }
        }
        length = bDevice.ProductByte[offset] & 0x7f;
        MSTRING(String.format("ProductInfo length AA %s", length));
         if (length == 0){
             return null;
         }
        if ((bDevice.ProductByte[offset] >> 7) == 0){                               // ascii或各种内码
            byte[] outData = new byte[length];
            MAPI.COPY_BUFF8(length, PRODUCT_TEXT_DATA + start, bDevice.ProductByte, 0, outData);
            MSTRING(String.format("ProductInfo getTextOffset %02x %02x %02x %02x", offset, bDevice.ProductByte[offset], start, length));
if(offset==0x65) {
MSTRING(String.format("ProductInfo AA %02x %02x %02x %02x", outData[0], outData[1], outData[2], outData[3]));
}
            String strCode = (bDevice.ProductByte[PRODUCT_CHAR_CODE] == FwbType.CODE_GB2312) ? "gbk" : "utf-8";
            try {
                return new String(outData, strCode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else {                                                                     // unicode
            MSTRING(String.format("ProductInfo length BB %s", length));
            length /= 2;
            char[] outData = new char[length];
            int counter = PRODUCT_TEXT_DATA + start;
            for (int index = 0; index < length; index++){
                outData[index] = (char)((bDevice.ProductByte[counter] & 0xff) |  ((bDevice.ProductByte[counter + 1]) << 8 & 0xff00));
//MSTRING(String.format("OffsetA %02x %02x", index, outData[index]));
                counter += 2;
            }
//            MSTRING(String.format("getTextOffsetB %02x %02x %02x %02x", offset, bDevice.ProductByte[offset], start, length));
            return new String(outData);
        }
        return null;
    }
    public String getBrand(BDevice bDevice) {
        return getTextOffset(bDevice, PRODUCT_BRAND_SIZE);
    }
    public String getModel(BDevice bDevice) {
        return getTextOffset(bDevice, PRODUCT_MODEL_SIZE);
    }
    public String getNickName(BDevice bDevice) {
        return getTextOffset(bDevice, PRODUCT_NICK_NAME);
    }
    public String getTxWifiName(BDevice bDevice) {
        return getTextOffset(bDevice, PRODUCT_TX_WIFI_NAME);
    }
    public String getTrueWifi(BDevice bDevice) {
        return getTextOffset(bDevice, PRODUCT_TRUE_WIFI);
    }


    public String getDateTime(BDevice bDevice, int offset){
        int dateTime = 0;
        if (bDevice.ProductByte != null) {
            dateTime = MAPI.BUFF8_DWORD(offset, bDevice.ProductByte);
        }
//        MSTRING(String.format("PRODUCT_DATE_VER %s %08x %02x%02x%02x%02x", offset, dateTime, inData[offset+0], inData[offset+1], inData[offset+2], inData[offset+3]));
        int tmp1 = (dateTime >> 16) / (12 * 31);
        int tmp2 = (dateTime >> 16) % (12 * 31);
        int tmp3 = dateTime & 0xffff;
        int tmp4 = tmp3 % (60 * 30);
        return String.format("%02d/%02d/%02d %02d:%02d:%02d", (tmp1 + 2010) - 2000, (tmp2 / 31)+1, (tmp2 % 31)+1, tmp3 / (60 * 30), tmp4 / 30, tmp4 % 30);
    }
    public String getVersion(BDevice bDevice, int offset){
        int version = 0;
        if (bDevice.ProductByte != null) {
            version = MAPI.BUFF8_DWORD(offset + 4, bDevice.ProductByte);
        }
//        MSTRING(String.format("PRODUCT_DATE_VER %s %08x %02x%02x%02x%02x", offset+4, version, inData[offset+4], inData[offset+5], inData[offset+6], inData[offset+7]));
        if (version > 1000000){
            return String.format("%d.%d.%d", version/1000000, (version%1000000)/1000, (version%1000000)%1000);
        }
        return String.format("%d.%d", version/1000, version%1000);
    }

    public boolean isKc35h(BDevice bDevice){
        return (bDevice.ProductByte != null) ? bDevice.ProductByte[PRODUCT_KCM_MODEL] == Kc3xType.KCM_MODEL_35H : false;
    }
    public boolean isKc32c(BDevice bDevice){
        return (bDevice.ProductByte != null) ? bDevice.ProductByte[PRODUCT_KCM_MODEL] == Kc3xType.KCM_MODEL_32C :false;
    }

}
