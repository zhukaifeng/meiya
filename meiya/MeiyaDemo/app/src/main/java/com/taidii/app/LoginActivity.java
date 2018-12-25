package com.taidii.app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taidii.app.model.LoginRsp;
import com.taidii.app.model.RefreshEvent;
import com.taidii.app.model.WXAccessTokenEntity;
import com.taidii.app.model.WXRefreshEntity;
import com.taidii.app.model.WXUserInfo;
import com.taidii.app.utils.LogUtils;
import com.taidii.app.utils.MD5;
import com.taidii.app.utils.SharePrefUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import okhttp3.Call;

import static android.util.Base64.NO_WRAP;
import static com.taidii.app.Constants.API_LOGIN;
import static com.taidii.app.Constants.APP_ID;
import static com.taidii.app.Constants.BASE_HTTP_PORT;
import static com.taidii.app.Constants.SECRET;
import static com.taidii.app.MyApplication.mWxApi;

/**
 * Created by zhukaifeng on 2018/11/30.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private AppCompatButton btn_login;
    protected Dialog loadDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        EventBus.getDefault().register(this);
        if (null != SharePrefUtils.getString("refresh_token") && !SharePrefUtils.getString("refresh_token").equals("")) {
            if (!mWxApi.isWXAppInstalled()) {
                Toast.makeText(LoginActivity.this,"请安装微信",Toast.LENGTH_SHORT);
            } else {
                long nowTime = System.currentTimeMillis();
                long loginTime = SharePrefUtils.getLong("wechat_login", 0);
                if ((nowTime - loginTime) / (1000 * 60) > (24 * 60 * 20)) {//重新刷新token 24 * 60 * 20
                    SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    req.state = "diandi_wx_login";
                    mWxApi.sendReq(req);
                } else {
                    getRefreshAccessToken();//刷新refresh_token
                }

            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:


                if (!mWxApi.isWXAppInstalled()) {
//                    Intent intent = new Intent(this, MyDialogActivity.class);
//                    startActivity(intent);
                } else {

                    long nowTime = System.currentTimeMillis();
                    long loginTime = SharePrefUtils.getLong("wechat_login", 0);

                    if ((nowTime - loginTime) / (1000 * 60) > (24 * 60 * 20)) {//重新刷新token
                        SendAuth.Req req = new SendAuth.Req();
                        req.scope = "snsapi_userinfo";
                        req.state = "diandi_wx_login";
                        mWxApi.sendReq(req);
                    } else {
                        getRefreshAccessToken();//刷新refresh_token
                    }

                }


//                CustomConfirmDialog.Builder builder = new CustomConfirmDialog.Builder(LoginActivity.this);
//                builder.setPositiveButton(new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//                builder.setMessage(String.format(getResources().getString(R.string.text_intent_wecchat_soft),"欢乐开"));
//                builder.setNegativeButton(new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//                builder.create().show();


                break;
        }
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThreadGetWXToken(RefreshEvent refreshEvent) {
        getAccessToken(refreshEvent.getCode());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getLoginToken() {
        showLoadDialog();
        String url = BASE_HTTP_PORT + API_LOGIN;

        String signature = "";
        String timeStamp = String.valueOf(getSecondTimestampTwo(new Date()));


        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Constants.APP_ID).append("|")
                .append(Constants.SECRET).append("|")
                .append(SharePrefUtils.getString("openid")).append("|")
                .append(SharePrefUtils.getString("unionid")).append("|")
                .append(timeStamp);
        String strBase64 = Base64.encodeToString(stringBuffer.toString().getBytes(), NO_WRAP);

        String strMd5 = MD5.Md5(strBase64);
        signature = strMd5;

        OkHttpUtils.get().url(url).addParams("app_id", Constants.APP_ID)
                .addParams("openid", SharePrefUtils.getString("openid"))
                .addParams("avatar", SharePrefUtils.getString("avatar"))
                .addParams("nickname", SharePrefUtils.getString("nickname"))
                .addParams("times", timeStamp)
                .addParams("signature", signature)
                .addParams("unionid", SharePrefUtils.getString("unionid"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.d("zkf e:" + e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.d("zkf response:" + response);
                        JsonParser parser = new JsonParser();
                        JsonObject json = parser.parse(response).getAsJsonObject();

                        if (json.has("code") && json.get("code").getAsInt() == 1) {
                            Gson gson = new Gson();
                            LoginRsp loginRsp = gson.fromJson(response, LoginRsp.class);

                            SharePrefUtils.saveString("login_token", loginRsp.getData().getToken());
                            SharePrefUtils.saveString("login_sessionid", loginRsp.getData().getSessionid());
                            SharePrefUtils.saveString("login_uid", loginRsp.getData().getUid());
                            SharePrefUtils.saveString("login_openid", loginRsp.getData().getOpenid());
                            cancelLoadDialog();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                });


    }

    public static int getSecondTimestampTwo(Date date) {
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime() / 1000);
        return Integer.valueOf(timestamp);
    }


    private void getRefreshAccessToken() {
        showLoadDialog();
        OkHttpUtils.get().url("https://api.weixin.qq.com/sns/oauth2/refresh_token")
                .addParams("appid", APP_ID)
                .addParams("refresh_token", SharePrefUtils.getString("refresh_token"))
                .addParams("grant_type", "refresh_token")
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        cancelLoadDialog();
                        LogUtils.d("请求错误..");
                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.d("response:" + response);
                        Gson gson = new Gson();
                        WXRefreshEntity refreshTokenEntity = gson.fromJson(response, WXRefreshEntity.class);
                        if (refreshTokenEntity != null) {
                            SharePrefUtils.saveString("refresh_token", refreshTokenEntity.getRefresh_token());
                            //  getUserInfo(accessTokenEntity);
                           getLoginToken();
                        } else {
                            LogUtils.d("获取失败");
                            SendAuth.Req req = new SendAuth.Req();
                            req.scope = "snsapi_userinfo";
                            req.state = "diandi_wx_login";
                            mWxApi.sendReq(req);
                        }
                        cancelLoadDialog();
                    }
                });


    }

    private void getAccessToken(String code) {
        showLoadDialog();
        OkHttpUtils.get().url("https://api.weixin.qq.com/sns/oauth2/access_token")
                .addParams("appid", APP_ID)
                .addParams("secret", SECRET)
                .addParams("code", code)
                .addParams("grant_type", "authorization_code")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        cancelLoadDialog();
                        LogUtils.d("请求错误..");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.d("response:" + response);
                        Gson gson = new Gson();
                        WXAccessTokenEntity accessTokenEntity = gson.fromJson(response, WXAccessTokenEntity.class);
                        if (accessTokenEntity != null) {
                            SharePrefUtils.saveString("refresh_token", accessTokenEntity.getRefresh_token());
                            SharePrefUtils.saveLong("wechat_login", System.currentTimeMillis());
                            getUserInfo(accessTokenEntity);
                        } else {
                            LogUtils.d("获取失败");
                        }
                        cancelLoadDialog();
                    }
                });


    }

    /**
     * 获取个人信息
     *
     * @param accessTokenEntity
     */
    private void getUserInfo(WXAccessTokenEntity accessTokenEntity) {
        showLoadDialog();
        OkHttpUtils.get()
                .url("https://api.weixin.qq.com/sns/userinfo")
                .addParams("access_token", accessTokenEntity.getAccess_token())
                .addParams("openid", accessTokenEntity.getOpenid())//openid:授权用户唯一标识
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        cancelLoadDialog();
                        LogUtils.d("获取错误..");
                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.d("userInfo:" + response);
                        Gson gson = new Gson();
                        WXUserInfo wxResponse = gson.fromJson(response, WXUserInfo.class);
                        LogUtils.d("微信登录资料已获取，后续未完成");

                        if (null != wxResponse) {
                            SharePrefUtils.saveString("openid", wxResponse.getOpenid());
                            SharePrefUtils.saveString("avatar", wxResponse.getHeadimgurl());
                            SharePrefUtils.saveString("nickname", wxResponse.getNickname());
                            SharePrefUtils.saveString("unionid", wxResponse.getUnionid());
                            getLoginToken();
                        }
                        cancelLoadDialog();
                    }
                });
    }


    protected synchronized void showLoadDialog() {
        if (loadDialog == null) {
            loadDialog = new Dialog(this, R.style.LoadingDialog);
            View progressContentView = LayoutInflater.from(this).inflate(R.layout
                    .layout_loading_dialog, null);
            ProgressBar pb = (ProgressBar) progressContentView.findViewById(R.id.pb);
            pb.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
            loadDialog.setContentView(progressContentView);
            loadDialog.setCancelable(BuildConfig.DEBUG);// Debug模式下可以取消Dialog
            loadDialog.setCanceledOnTouchOutside(false);
        }
        if (!loadDialog.isShowing()) {
            loadDialog.show();
        }
        loadDialogShowCount++;
    }

    private volatile int loadDialogShowCount = 0;

    protected void cancelLoadDialog() {
        cancelLoadDialog(false);
    }

    protected synchronized void cancelLoadDialog(boolean force) {
        if (force) {
            loadDialogShowCount = 0;
        }
        loadDialogShowCount--;
        if (loadDialog != null && loadDialogShowCount <= 0) {
            loadDialog.cancel();
        }
    }


}
