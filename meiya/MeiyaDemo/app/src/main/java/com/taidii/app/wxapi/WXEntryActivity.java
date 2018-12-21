package com.taidii.app.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.taidii.app.model.RefreshEvent;
import com.taidii.app.model.WXAccessTokenEntity;
import com.taidii.app.model.WXUserInfo;
import com.taidii.app.utils.LogUtils;
import com.taidii.app.utils.SharePrefUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import static com.taidii.app.Constants.APP_ID;
import static com.taidii.app.Constants.SECRET;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
//	private static final String APP_SECRET = Constants.SECRET;
	private IWXAPI mWeixinAPI;
	public static final String WEIXIN_APP_ID = APP_ID;
	private static String uuid;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWeixinAPI = WXAPIFactory.createWXAPI(this, WEIXIN_APP_ID, true);
		mWeixinAPI.handleIntent(this.getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		mWeixinAPI.handleIntent(intent, this);//必须调用此句话
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mWeixinAPI.handleIntent(data,this);
	}

	//微信发送的请求将回调到onReq方法
	@Override
	public void onReq(BaseReq req) {
		LogUtils.d("onReq");
	}



	//发送到微信请求的响应结果
	@Override
	public void onResp(BaseResp resp) {
		LogUtils.d("zkf onResp :" +resp.errCode );
		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				LogUtils.d("zkf ERR_OK");
				//发送成功
				SendAuth.Resp sendResp = (SendAuth.Resp) resp;
				if (sendResp != null) {
					String code = sendResp.code;
					EventBus.getDefault().post(new RefreshEvent(code));
				}
				finish();
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				//发送取消
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				LogUtils.d("zkf ERR_AUTH_DENIED");
				//发送被拒绝
				break;
			default:
				//发送返回
				break;
		}
		if (resp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
			WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp) resp;
			String extraData =launchMiniProResp.extMsg; //对应小程序组件 <button open-type="launchApp"> 中的 app-parameter 属性
			LogUtils.d("zkf extraData:" + extraData);
		}

	}




}