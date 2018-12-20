package com.taidii.app;

public class Constants {
    public static final String APP_ID = "wx236cf7677b85c759";
    public static final String SECRET = "eef2ca17e25e2d7a6992424e4825949a";
    public static final String BASE_HTTP_PORT = "https://dev.changwb.cn";

    public static final String API_LOGIN = "/comapi/login/appreg";
    public static final String API_GET_INFO = "/comapi/user/getMyInfo";
    public static final String API_GET_NOTICE = "/comapi/user/getMyNotice";
    public static final String API_WEBVIEW_URL =
            "/spread/myapp/appindex#/?uid=%1$s&app_id=%2$s&token=%3$s&sessionid=%4$s&openid=%5$s&model=android";

    public static class ShowMsgActivity {
		public static final String STitle = "showmsg_title";
		public static final String SMessage = "showmsg_message";
		public static final String BAThumbData = "showmsg_thumb_data";
	}
}
