package com.github.tvbox.osc.ui.dialog;

import android.content.Context;

import androidx.annotation.NonNull;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.R;

import org.jetbrains.annotations.NotNull;

public class AboutDialog extends BaseDialog {
    private TextView tvAboutText;
    public AboutDialog(@NonNull @NotNull Context context) {
        super(context);
        setContentView(R.layout.dialog_about);
        tvAboutText = findViewById(R.id.tvAboutText);
        String msg = "本软件只提供聚合展示功能，所有资源来自网上, 软件不参与任何制作, 上传, 储存, 下载等内容. 软件仅供学习参考, 请于安装后24小时内删除。\n\n";
        msg += "当前版本号：" + ApiConfig.version + "\n";
        if(!ApiConfig.smsg.isEmpty()) msg += ApiConfig.smsg + "\n";
        if(!ApiConfig.dmsg.isEmpty()) msg += ApiConfig.dmsg + "\n";
        tvAboutText.setText(msg);
    }
}