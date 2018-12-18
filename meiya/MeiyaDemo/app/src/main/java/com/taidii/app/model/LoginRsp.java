package com.taidii.app.model;

import java.io.Serializable;

/**
 * Created by zhukaifeng on 2018/12/2.
 */

public class LoginRsp implements Serializable{


	/**
	 * code : 1
	 * data : {"token":"MTU0NTE5MDQwNpnaemWXjbaihJmC24axftSCobuasbbNmL6Ll2mChY1kgbWBnYWfrmqOqn6VhqV6lYehumGy3LjRrqVgdA","sessionid":"s_5c186a86556d51545104006","uid":"37","openid":"oY_ES1fzeOaU0udXyAVgq9KNZlZY"}
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

	public static class DataBean implements Serializable{
		/**
		 * token : MTU0NTE5MDQwNpnaemWXjbaihJmC24axftSCobuasbbNmL6Ll2mChY1kgbWBnYWfrmqOqn6VhqV6lYehumGy3LjRrqVgdA
		 * sessionid : s_5c186a86556d51545104006
		 * uid : 37
		 * openid : oY_ES1fzeOaU0udXyAVgq9KNZlZY
		 */

		private String token;
		private String sessionid;
		private String uid;
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

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
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
