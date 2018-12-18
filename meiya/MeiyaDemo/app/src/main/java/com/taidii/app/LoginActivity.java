package com.taidii.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taidii.app.model.LoginRsp;
import com.taidii.app.model.WXUserInfo;
import com.taidii.app.utils.LogUtils;
import com.taidii.app.utils.MD5;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;

import static com.taidii.app.Constants.API_LOGIN;
import static com.taidii.app.Constants.BASE_HTTP_PORT;

/**
 * Created by zhukaifeng on 2018/11/30.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


	private AppCompatButton btn_login;
	public static IWXAPI mWxApi;
	private WXUserInfo mUserInfo;

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

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(WXUserInfo userInfo){
		LogUtils.d("url:" + userInfo.getHeadimgurl());
		if (null != userInfo)
			getLoginToken(userInfo);

//
//		Intent intent = new Intent(this, MainActivity.class);
//		intent.putExtra("userinfo",userInfo);
//		startActivity(intent);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private void getLoginToken(WXUserInfo userInfo) {

		String url = BASE_HTTP_PORT + API_LOGIN;

		String 	signature = ""	;
		String timeStamp = String.valueOf(getSecondTimestampTwo(new Date()));

		/*String[] strArray = new String[];
		strArray[0] = Constants.APP_ID;
		strArray[1] = Constants.SECRET;
		strArray[2] = userInfo.getOpenid();
		strArray[3] = userInfo.getUnionid();
		strArray[4] = timeStamp;*/
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(Constants.APP_ID).append("|")
				.append(Constants.SECRET).append("|")
				.append(userInfo.getOpenid()).append("|")
				.append(userInfo.getUnionid()).append("|")
				.append(timeStamp);
		LogUtils.d("zkf stringBuffer :" + stringBuffer.toString());


		String strBase64 = java.util.Base64.getEncoder().encodeToString(stringBuffer.toString().getBytes());
		LogUtils.d(	"zkf strBase64 :" + strBase64);

		String strMd5 = MD5.Md5(strBase64);
		signature = strMd5;

		LogUtils.d(	"zkf strMd5 :" + strMd5);

		OkHttpUtils.get().url(url).addParams("app_id",Constants.APP_ID)
				.addParams("openid",userInfo.getOpenid())
				.addParams("avatar",userInfo.getHeadimgurl())
				.addParams("nickname",userInfo.getNickname())
				.addParams("times", timeStamp)
				.addParams("signature",signature)
				.addParams("unionid",userInfo.getUnionid())
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

						if (json.has("code") && json.get("code").getAsInt() == 1){
							Gson gson = new Gson();
							LoginRsp loginRsp = gson.fromJson(response,LoginRsp.class);
							Intent intent = new Intent(LoginActivity.this,MainActivity.class);
							intent.putExtra("logininfo",loginRsp);
							startActivity(intent);
						}


					}
				});



	}

	public static int getSecondTimestampTwo(Date date){
		if (null == date) {
			return 0;
		}
		String timestamp = String.valueOf(date.getTime()/1000);
		return Integer.valueOf(timestamp);
	}



}
