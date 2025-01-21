package com.github.spiderbox.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.spiderbox.R;
import com.github.spiderbox.bean.VodInfo;
import com.github.spiderbox.cache.RoomDataManger;
import com.github.spiderbox.cache.VodCollect;
import com.github.spiderbox.ui.activity.CollectActivity;
import com.github.spiderbox.ui.activity.HistoryActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfirmClearDialog extends BaseDialog {
    private final TextView tvYes;
    private final TextView tvNo;

    public ConfirmClearDialog(@NonNull @NotNull Context context, String type) {
        super(context);
        setContentView(R.layout.dialog_confirm);
        setCanceledOnTouchOutside(true);
        tvYes = findViewById(R.id.btnConfirm);
        tvNo = findViewById(R.id.btnCancel);

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if removing all Favorites
                if (type == "Collect") {
                    List<VodCollect> vodInfoList = new ArrayList<>();
                    CollectActivity.collectAdapter.setNewData(vodInfoList);
                    CollectActivity.collectAdapter.notifyDataSetChanged();
                    RoomDataManger.deleteVodCollectAll();
                    // if removing all History
                } else if (type == "History") {
                    List<VodInfo> vodInfoList = new ArrayList<>();
                    HistoryActivity.historyAdapter.setNewData(vodInfoList);
                    HistoryActivity.historyAdapter.notifyDataSetChanged();
                    RoomDataManger.deleteVodRecordAll();
                }

                ConfirmClearDialog.this.dismiss();
            }
        });
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmClearDialog.this.dismiss();
            }
        });
    }

}