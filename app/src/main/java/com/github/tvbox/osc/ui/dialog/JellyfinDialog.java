package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;
import com.lzy.okgo.OkGo;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

public class JellyfinDialog extends BaseDialog{
    private EditText etUrl;
    private EditText etUsername;
    private EditText etPassword;


    public JellyfinDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_jellyfin);
        setCanceledOnTouchOutside(false);

        etUrl.setText(Hawk.get("tvbox_jellyfin_url",""));
        etUsername.setText(Hawk.get("tvbox_jellyfin_username",""));
        etPassword.setText(Hawk.get("tvbox_jellyfin_password",""));

        findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void validUrl(String url){
        if(url.startsWith("http:") || url.startsWith("https:")){
            OkGo.<String>get(url);
        }
    }
}
