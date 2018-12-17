package com.taidii.app.model;

/**
 * Created by zhukaifeng on 2018/12/2.
 */

public class LoginRsp {


	/**
	 * code : 1
	 * data : {"token":"MTU0MzU0NDg2OZuqrWKauISctJ15qrKwp8qviHqdjJqzn4y6tpe8ZYPLhKqsZ4LLiZy_oI1jv451krB4fpmLh6acmbeeoA","sessionid":"s_5bfe445c071461543390300","uid":103,"openid":"1234"}
	 * msg : 操作成功
	 */

	private int code;
	private DataBean data;
	private String msg;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public static class DataBean {
		/**
		 * token : MTU0MzU0NDg2OZuqrWKauISctJ15qrKwp8qviHqdjJqzn4y6tpe8ZYPLhKqsZ4LLiZy_oI1jv451krB4fpmLh6acmbeeoA
		 * sessionid : s_5bfe445c071461543390300
		 * uid : 103
		 * openid : 1234
		 */

		private String token;
		private String sessionid;
		private int uid;
		private String openid;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getSessionid() {
			return sessionid;
		}

		public void setSessionid(String sessionid) {
			this.sessionid = sessionid;
		}

		public int getUid() {
			return uid;
		}

		public void setUid(int uid) {
			this.uid = uid;
		}

		public String getOpenid() {
			return openid;
		}

		public void setOpenid(String openid) {
			this.openid = openid;
		}
	}
}
