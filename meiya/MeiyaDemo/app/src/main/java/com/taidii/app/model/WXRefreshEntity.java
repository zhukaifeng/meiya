package com.taidii.app.model;

/**
 * Created by zhukaifeng on 2018/12/21.
 */

public class WXRefreshEntity {


    /**
     * openid : oY_ES1fzeOaU0udXyAVgq9KNZlZY
     * access_token : 16_PAUaFCCdsNEcFQnCHeY7ptHL0KzO2MtSosHiA8k6vKoDJ2SALhioA8lf-1oCIf6fGIwkBy7wI0KxQQ6H3zwbv-T9W0G-NTwPPsJGNL0r438
     * expires_in : 7200
     * refresh_token : 16_-r7oPSVaLFlzIXZkZwbsSqs96P5Fa1T6NSfzz_GLmSDyfHJfPeclsFKLWQtAFyVemHQZqtEQS4vpNb_Pgt8_l78uAA0_iTS7ehk7kMum7jM
     * scope : snsapi_base,snsapi_userinfo,
     */

    private String openid;
    private String access_token;
    private int expires_in;
    private String refresh_token;
    private String scope;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
