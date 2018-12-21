package com.taidii.app;

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
//    private WXUserInfo mUserInfo;
//    private LoginRsp loginRsp;
    private ImageView iv_more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        loginRsp = (LoginRsp) getIntent().getSerializableExtra("logininfo");
//        mUserInfo = (WXUserInfo) getIntent().getSerializableExtra("wxuserinfo");

        mBdwebview = findViewById(R.id.bdwebview);
        iv_more = findViewById(R.id.iv_more);
        iv_more.setVisibility(View.GONE);
        title = findViewById(R.id.title);
        title.setText(getResources().getString(R.string.app_name));
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        linear_back = findViewById(R.id.linear_back);
        linear_back.setOnClickListener(this);
        linear_back.setVisibility(View.GONE);

        WebSettings settings = mBdwebview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//
//        settings.setUseWideViewPort(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        mBdwebview.setHorizontalScrollBarEnabled(false);
        mBdwebview.setVerticalScrollBarEnabled(false);

        String url = Constants.BASE_HTTP_PORT + String.format(Constants.API_WEBVIEW_URL, SharePrefUtils.getString("login_uid"),
                Constants.APP_ID, SharePrefUtils.getString("login_token"), SharePrefUtils.getString("login_sessionid"), SharePrefUtils.getString("login_openid"));
        LogUtils.d("zkf url:" + url);
        String testUrl = "https://www.baidu.com/s?wd=android%20%E5%8F%8C%E5%87%BB%E9%80%80%E5%87%BA%E5%BA%94%E7%94%A8" +
                "&rsv_spt=1&rsv_iqid=0xa0c44ff500003c78&issp=1&f=3&rsv_bp=1&rsv_idx=2&ie=utf-8&rqlang=cn&tn=baiduhome_pg&rsv_enter=0&oq=android%2520%25E5%25BE%25AE%25E4%25BF%25A1%25E7%2599%25BB%25E5%25BD%2595&rsv_t=f18bzUmGnb6KsjEl%2FN6WYSV%2BH02eco55EwgFE9WD4Oy62XiW3W3vgnocPNiHORrB1TpA&rsv_pq=d5e7971400005eb4&inputT=3864&rsv_sug3=413&rsv_sug1=245&rsv_sug7=100&rsv_sug2=1&prefixsug=android%2520%25E5%258F%258C%25E5%2587%25BB&rsp=1&rsv_sug4=5615";
        mBdwebview.loadUrl(url);//显示H5页面
        mBdwebview.addBridgeInterface(new MyJavaSctiptInterface(this, mBdwebview));

        mBdwebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mBdwebview.canGoBack()){
                    linear_back.setVisibility(View.VISIBLE);
                }else {
                    linear_back.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);
            }
        });

        getMyInfo();
        getNoticeList();
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
