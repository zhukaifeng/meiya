package com.taidii.app;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itheima.view.BridgeWebView;
import com.taidii.app.model.LoginRsp;
import com.taidii.app.model.UserInfo;
import com.taidii.app.model.WXUserInfo;
import com.taidii.app.utils.LogUtils;
import com.taidii.app.utils.SharePrefUtils;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

import static com.taidii.app.MyApplication.mWxApi;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private BridgeWebView mBdwebview;
    private TextView title;
    private LinearLayout linear_back;
    private ProgressBar progressbar;
    //    private WXUserInfo mUserInfo;
//    private LoginRsp loginRsp;
    private ImageView iv_more;

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        loginRsp = (LoginRsp) getIntent().getSerializableExtra("logininfo");
//        mUserInfo = (WXUserInfo) getIntent().getSerializableExtra("wxuserinfo");
        progressbar = findViewById(R.id.progressbar);
        mBdwebview = findViewById(R.id.bdwebview);
        iv_more = findViewById(R.id.iv_more);
        iv_more.setVisibility(View.GONE);
        title = findViewById(R.id.title);
        title.setText(getResources().getString(R.string.app_name));

        linear_back = findViewById(R.id.linear_back);
        linear_back.setOnClickListener(this);
        linear_back.setVisibility(View.GONE);

        WebSettings settings = mBdwebview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setTextZoom(100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        mBdwebview.setHorizontalScrollBarEnabled(false);
        mBdwebview.setVerticalScrollBarEnabled(false);

        String url = Constants.BASE_HTTP_PORT + String.format(Constants.API_WEBVIEW_URL, SharePrefUtils.getString("login_uid"),
                Constants.APP_ID, SharePrefUtils.getString("login_token"), SharePrefUtils.getString("login_sessionid"), SharePrefUtils.getString("login_openid"));

        mBdwebview.loadUrl(url);
        mBdwebview.addBridgeInterface(new MyJavaSctiptInterface(this, mBdwebview));
        mBdwebview.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    // 网页加载完成
                    progressbar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    // 加载中
                    progressbar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    progressbar.setProgress(newProgress);//设置进度值
                }

            }
        });
        mBdwebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mBdwebview.canGoBack()) {
                    linear_back.setVisibility(View.VISIBLE);
                } else {
                    linear_back.setVisibility(View.GONE);
                }
            }



            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);
            }
        });

//        getMyInfo();
//        getNoticeList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_back:
                if (mBdwebview.canGoBack()) {
                    mBdwebview.goBack();
                }
                break;
        }
    }


    private void getMyInfo() {

        String url = Constants.BASE_HTTP_PORT + Constants.API_GET_INFO;

        OkHttpUtils.get().url(url)
                .addParams("token", SharePrefUtils.getString("login_token"))
                .addParams("sessionid", SharePrefUtils.getString("login_sessionid"))
                .addParams("uid", SharePrefUtils.getString("login_uid"))
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
                .addParams("token", SharePrefUtils.getString("login_token"))
                .addParams("sessionid", SharePrefUtils.getString("login_sessionid"))
                .addParams("uid", SharePrefUtils.getString("login_uid"))
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mBdwebview.canGoBack()) {
            mBdwebview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
