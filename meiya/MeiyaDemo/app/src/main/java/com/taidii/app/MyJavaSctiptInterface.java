package com.taidii.app;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.taidii.app.utils.LogUtils;
import com.taidii.app.view.CustomConfirmDialog;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;

import org.json.JSONException;
import org.json.JSONObject;

import static com.taidii.app.MyApplication.mWxApi;

/**
 * Created by Mr_Ashin on 2018/12/2.
 */

class MyJavaSctiptInterface {


    private WebView webView;
    private Activity mActivity;

    public MyJavaSctiptInterface(Activity mContext, WebView webView) {
        this.mActivity = mContext;
        this.webView = webView;
    }

    /**
     * 统一分发js调用android分发
     */
    public void send(String[] jsons) {
        final String str = jsons[0];
        Log.d("result : ", "zkf  str  :" + str);
        try {
            JSONObject json = new JSONObject(str);
            String action = json.optString("action");//js传递过来的动作，比如callPhone代表拨号，share2QQ代表分享到QQ，其实就是H5和android通信协议（自定义的）
            if (!TextUtils.isEmpty(action)) {
                if (action.equals("showCallPhoneApp")) {//底部弹出拨号对话框


//                    CustomConfirmDialog.Builder builder = new CustomConfirmDialog.Builder(mActivity);
//                    builder.setPositiveButton(new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    });
//                    builder.setMessage(String.format(mActivity.getResources().getString(R.string
//                            .text_intent_wecchat_soft), "欢乐开"));
//                    builder.setNegativeButton(new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    });
//                    builder.create().show();
                    Log.d("result : ", "zkf  showCallPhoneDialogsdddd  :" + json.optString("gh_id"));
                    String wxId = json.optString("gh_id");
                    WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
                    req.userName = wxId; // 填小程序原始id
//                req.path = path;                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
                    req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
                    mWxApi.sendReq(req);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
