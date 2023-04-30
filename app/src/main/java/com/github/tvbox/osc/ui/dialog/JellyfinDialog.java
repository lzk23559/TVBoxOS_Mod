package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.orhanobut.hawk.Hawk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JellyfinDialog extends BaseDialog {

    private EditText etUrl;
    private EditText etUsername;
    private EditText etPassword;


    public JellyfinDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_jellyfin);
        setCanceledOnTouchOutside(false);

        etUrl = findViewById(R.id.jellyfin_url);
        etUsername = findViewById(R.id.jellyfin_username);
        etPassword = findViewById(R.id.jellyfin_password);

        etUrl.setText(Hawk.get("tvbox_jellyfin_url", ""));
        etUsername.setText(Hawk.get("tvbox_jellyfin_username", ""));
        etPassword.setText(Hawk.get("tvbox_jellyfin_password", ""));

        findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverUrl = etUrl.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                validUrl(serverUrl, new CallBack() {
                    @Override
                    public void onSucc() {
                        Hawk.put("tvbox_jellyfin_url",serverUrl);
                        authenticateByName(serverUrl, username, password, new CallBack() {
                            @Override
                            public void onSucc() {
                                Hawk.put("tvbox_jellyfin_username",username);
                                Hawk.put("tvbox_jellyfin_password",password);
                                dismiss();
                            }

                            @Override
                            public void onfaile(String msg) {
                                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onfaile(String msg) {
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 验证服务器地址
     * @param url
     * @param cb
     */
    public void validUrl(String url, CallBack cb) {
        if (url.startsWith("http:") || url.startsWith("https:")) {
            OkGo.<String>get(url + "/system/info/public")
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            try {
                                JSONObject jo = new JSONObject(response.body());
                                String serverId = jo.getString("Id");
                                if (serverId != null && serverId != "") {
                                    cb.onSucc();
                                }
                            } catch (JSONException e) {
                                cb.onfaile(e.getMessage());
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            cb.onfaile(response.body());
                        }
                    });
        }else{
            cb.onfaile("");
        }
    }

    /**
     * 验证用户名密码
     * @param url
     * @param username
     * @param password
     * @param cb
     */
    private void authenticateByName(String url,String username, String password,CallBack cb) {
        String reqjson = "{\"Username\":\"" + username + "\",\"Pw\":\"" + password + "\"}";
        OkGo.<String>post(url + "/Users/authenticatebyname")
                .upJson(reqjson)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject userObj = null;
                        String UserId = null;
                        String Token = null;
                        try {
                            userObj = new JSONObject(response.body());
                            UserId = userObj.getJSONObject("User").getString("Id");
                            Token = userObj.getString("AccessToken");
                            if (Token != null) {
                                cb.onSucc();
                            }else{
                                cb.onfaile("用户名或者密码错误！");
                            }
                        } catch (JSONException e) {
                            cb.onfaile(e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        cb.onfaile(response.body());
                    }
                });
    }

    interface CallBack {
        void onSucc();

        void onfaile(String msg);
    }
}
