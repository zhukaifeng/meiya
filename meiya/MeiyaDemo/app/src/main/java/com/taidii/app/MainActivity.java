package com.taidii.app;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itheima.view.BridgeWebView;
import com.taidii.app.model.WXUserInfo;
import com.taidii.app.utils.LogUtils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private BridgeWebView mBdwebview;
    private TextView title;
    private LinearLayout linear_back;
    private WXUserInfo mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserInfo = (WXUserInfo)getIntent().getSerializableExtra("userinfo");

        mBdwebview = findViewById(R.id.bdwebview);
        title = findViewById(R.id.title);
        linear_back = findViewById(R.id.linear_back);
        linear_back.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBdwebview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mBdwebview.loadUrl("https://dev.news.meiyashop.com.cn/spread/myapp/appindex#/?uid=87&app_id=wxe08d5bf1d2a21b13&token=123&sessionid=456");//显示H5页面
        mBdwebview.addBridgeInterface(new MyJavaSctiptInterface( this));



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linear_back:
                LogUtils.d("zkf click bacck");
                break;
        }
    }
}
