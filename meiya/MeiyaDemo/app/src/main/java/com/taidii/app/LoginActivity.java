package com.taidii.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.taidii.app.model.WXUserInfo;
import com.taidii.app.utils.LogUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zhukaifeng on 2018/11/30.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


	private AppCompatButton btn_login;
	public static IWXAPI mWxApi;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		btn_login = findViewById(R.id.btn_login);
		btn_login.setOnClickListener(this);
		registerToWX();
		EventBus.getDefault().register(this);


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
//                    final SendAuth.Req req = new SendAuth.Req();
//                    req.scope = "snsapi_userinfo";
//                    req.state = "wechat_sdk_demo_test";
//                    iwxapi.sendReq(req);
					SendAuth.Req req = new SendAuth.Req();
					req.scope = "snsapi_userinfo";
					req.state = "diandi_wx_login";
					mWxApi.sendReq(req);
				}


//                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                startActivity(intent);


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

	private void registerToWX() {
		mWxApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
		mWxApi.registerApp(Constants.APP_ID);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtils.d("zkf requestCode:" + requestCode);
		if (resultCode == 0) {
			String headUrl = data.getStringExtra("headUrl");
			LogUtils.d("url:" + headUrl);
			Intent intent = new Intent(this, MainActivity.class);
			// Glide.with(WXLoginActivity.this).load(headUrl).into(ivHead);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(WXUserInfo event){
		LogUtils.d("url:" + event.getHeadimgurl());

		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}



}
