package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.github.tvbox.osc.R;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

public class DescDialog extends BaseDialog {    

    public DescDialog(@NonNull @NotNull Context context) {
        super(context);       
        setContentView(R.layout.dialog_desc);
    }
    
    public void setDescribe(String describe) {
    	TextView tvDescribe = findViewById(R.id.describe);
        tvDescribe.setText("        内容简介：" + describe);  //xuameng
        tvDescribe.requestFocus();
        tvDescribe.requestFocusFromTouch();
    }

    private void init(Context context) {
        EventBus.getDefault().register(this);
        setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                EventBus.getDefault().unregister(this);
            }
        });
    }
}