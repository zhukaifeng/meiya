package com.taidii.app.model;

import java.util.List;

/**
 * Created by zhukaifeng on 2018/12/2.
 */

public class NoticeRsp {


	/**
	 * code : 1
	 * data : [{"id":1,"app_id":"wx236cf7677b85c759","content":"nihao","start_time":1543383000,"end_time":1553583057,"sort":1,"status":1,"create_time":1543383057,"update_time":1543383057,"delete_time":null}]
	 * msg : 操作成功
	 */

	private int code;
	private String msg;
	private List<DataBean> data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<DataBean> getData() {
		return data;
	}

	public void setData(List<DataBean> data) {
		this.data = data;
	}

	public static class DataBean {
		/**
		 * id : 1
		 * app_id : wx236cf7677b85c759
		 * content : nihao
		 * start_time : 1543383000
		 * end_time : 1553583057
		 * sort : 1
		 * status : 1
		 * create_time : 1543383057
		 * update_time : 1543383057
		 * delete_time : null
		 */

		private int id;
		private String app_id;
		private String content;
		private int start_time;
		private int end_time;
		private int sort;
		private int status;
		private int create_time;
		private int update_time;
		private Object delete_time;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getApp_id() {
			return app_id;
		}

		public void setApp_id(String app_id) {
			this.app_id = app_id;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public int getStart_time() {
			return start_time;
		}

		public void setStart_time(int start_time) {
			this.start_time = start_time;
		}

		public int getEnd_time() {
			return end_time;
		}

		public void setEnd_time(int end_time) {
			this.end_time = end_time;
		}

		public int getSort() {
			return sort;
		}

		public void setSort(int sort) {
			this.sort = sort;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public int getCreate_time() {
			return create_time;
		}

		public void setCreate_time(int create_time) {
			this.create_time = create_time;
		}

		public int getUpdate_time() {
			return update_time;
		}

		public void setUpdate_time(int update_time) {
			this.update_time = update_time;
		}

		public Object getDelete_time() {
			return delete_time;
		}

		public void setDelete_time(Object delete_time) {
			this.delete_time = delete_time;
		}
	}
}
