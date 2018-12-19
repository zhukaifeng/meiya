package com.taidii.app;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itheima.view.BridgeWebView;
import com.taidii.app.model.LoginRsp;
import com.taidii.app.model.UserInfo;
import com.taidii.app.model.WXUserInfo;
import com.taidii.app.utils.LogUtils;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

import static com.taidii.app.MyApplication.mWxApi;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private BridgeWebView mBdwebview;
    private TextView title;
    private LinearLayout linear_back;
    private WXUserInfo mUserInfo;
    private LoginRsp loginRsp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginRsp = (LoginRsp) getIntent().getSerializableExtra("logininfo");
        mUserInfo = (WXUserInfo) getIntent().getSerializableExtra("wxuserinfo");

        mBdwebview = findViewById(R.id.bdwebview);
        title = findViewById(R.id.title);
        title.setText("福利之家");
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
                req.userName = "gh_6688012ca93d"; // 填小程序原始id
//                req.path = path;                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
                req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
                mWxApi.sendReq(req);
            }
        });
        linear_back = findViewById(R.id.linear_back);
        linear_back.setOnClickListener(this);

        WebSettings settings = mBdwebview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }




        String url = Constants.BASE_HTTP_PORT + String.format(Constants.API_WEBVIEW_URL, loginRsp.getData().getUid(),
                Constants.APP_ID, loginRsp.getData().getToken(), loginRsp.getData().getSessionid(),mUserInfo.getOpenid());
        LogUtils.d("zkf url:" + url);

        mBdwebview.loadUrl(url);//显示H5页面
        mBdwebview.addBridgeInterface(new MyJavaSctiptInterface(this));

        getMyInfo();
        getNoticeList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_back:
                LogUtils.d("zkf click bacck");
                break;
        }
    }


    private void getMyInfo() {

        String url = Constants.BASE_HTTP_PORT + Constants.API_GET_INFO;

        OkHttpUtils.get().url(url)
                .addParams("token", loginRsp.getData().getToken())
                .addParams("sessionid", loginRsp.getData().getSessionid())
                .addParams("uid", loginRsp.getData().getUid())
                .addParams("app_id", Constants.APP_ID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.d("zkf e:" + e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.d("zkf response :" + response);
                        JsonParser parser = new JsonParser();
                        JsonObject json = parser.parse(response).getAsJsonObject();

                        if (json.has("code") && json.get("code").getAsInt() == 1) {
                            Gson gson = new Gson();
                            UserInfo userInfo = gson.fromJson(response, UserInfo.class);
                        }
                    }
                });


    }


    private void getNoticeList() {

        String url = Constants.BASE_HTTP_PORT + Constants.API_GET_NOTICE;

        OkHttpUtils.get().url(url)
                .addParams("token", loginRsp.getData().getToken())
                .addParams("sessionid", loginRsp.getData().getSessionid())
                .addParams("uid", loginRsp.getData().getUid())
                .addParams("app_id", Constants.APP_ID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.d("zkf e:" + e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.d("zkf response :" + response);
                        JsonParser parser = new JsonParser();
                        JsonObject json = parser.parse(response).getAsJsonObject();

                        if (json.has("code") && json.get("code").getAsInt() == 1) {
//                            Gson gson = new Gson();
//                            UserInfo userInfo = gson.fromJson(response, UserInfo.class);
                        }
                    }
                });


    }


}
