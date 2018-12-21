package com.taidii.app.model;

/**
 * Created by zhukaifeng on 2018/12/21.
 */

public class RefreshEvent {


    private  String code;

    public RefreshEvent(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
