package ltd.kcdevice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import ltd.kcdevice.R;

public class ChannelS1Group extends androidx.constraintlayout.widget.ConstraintLayout {
    public TextView tvVolumeS1;

    public ChannelS1Group(Context context) {
        this(context, null);
    }
    public ChannelS1Group(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.group_channel_s1, this, true);
        tvVolumeS1 = (TextView)findViewById(R.id.tvVolumeS1);
//        MLOG("VolumeS1Group tvVolumeS1 "+tvVolumeS1);
    }
    public void setText(String text){
        tvVolumeS1.setText(text);
    }
}