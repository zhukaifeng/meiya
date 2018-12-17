package com.taidii.app;

import android.app.Activity;

/**
 * Created by Mr_Ashin on 2018/12/2.
 */

class MyJavaSctiptInterface {



	private Activity mActivity;

	public MyJavaSctiptInterface(Activity mActivity) {
		this.mActivity = mActivity;
	}

	public void callPhone(String[] strs){//
//		JSONObject jsonObject = new JSONObject(strs[0]);
//		String phone = jsonObject.optString("phone");
//		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
//		mActivity.startActivity(intent);
	}

}
