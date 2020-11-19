package ltd.kcdevice.device;

import androidx.annotation.ArrayRes;

import ltd.advskin.MSKIN;
import ltd.kcdevice.R;
import ltd.kcdevice.base.Kc3xType;
import main.MAPI;

import static main.MAPI.MERROR;
import static main.MAPI.MSTRING;

public class KcmText {
    // 所有的页面都接受同样的方式打开LOG
    public static boolean logEnable;
    public void MLOG(String text) {
        if (logEnable) {
            MSTRING(MAPI.GET_LOG_NAME(this) + text);
        }
    }

    private String findText(@ArrayRes int id, int value){
        String[] stringArray = MSKIN.getStringArray(id);
        for (int index = 0; index < stringArray.length; index++){
            String[] strings = stringArray[index].split(",");
//            MLOG(String.format("stringsA %s %s %s", strings.length, strings[0], strings[1]));
            if (strings.length == 2 && strings[0].length() == 2){
                int g4Local_1 = MAPI.ASCII_HEX(strings[0].charAt(0));
                int g4Local_2 = MAPI.ASCII_HEX(strings[0].charAt(1));
                g4Local_1 = (g4Local_1 << 4) | g4Local_2;
//                MLOG(String.format("stringsAB %s %s %s", g4Local_1, g4Local_2, value));
                if (g4Local_1 == value){
                    return strings[1];
                }
            }else {
                MERROR("KcmText findText need: ff,text");
            }
        }
        return null;
    }


    public String getRmAudCtrText(Kc3xType kc3xType, int value) {      // 读取指定电器的寄存器数值对应的文字
        switch (kc3xType) {
            case KCM_INPUT_SOURCE: return findText(R.array.KCM_INPUT_SOURCE, value);
            case KCM_LISTEN_MODE: return findText(R.array.KCM_LISTEN_MODE, value);
            case KCM_EQ_SELECT: return findText(R.array.KCM_EQ_SELECT, value);
            case KCM_SRC_FORMAT: return findText(R.array.KCM_SRC_FORMAT, value);
            case RAC_RD_SD_QTY:
                return null;
            default:
                break;
        }
        return null;
    }
}
