package com.taidii.app.model;

/**
 * Created by zhukaifeng on 2018/12/18.
 */

public class UserInfo {


    /**
     * code : 1
     * data : {"nickname":"豆豆","avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eppjtQBRSh1coJ58JDCpHab3C1rSbht0vic7vFH7M6JVvuu5ureMQxBwuic1CvtpgVkgvibtLMlonxHQ/132","realname":"","alipay":"","uid":37,"openid":"oY_ES1fzeOaU0udXyAVgq9KNZlZY","is_new":1,"money":"0.00","total_money":"0.00","times":0,"unionid":"1"}
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
         * nickname : 豆豆
         * avatar : http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eppjtQBRSh1coJ58JDCpHab3C1rSbht0vic7vFH7M6JVvuu5ureMQxBwuic1CvtpgVkgvibtLMlonxHQ/132
         * realname :
         * alipay :
         * uid : 37
         * openid : oY_ES1fzeOaU0udXyAVgq9KNZlZY
         * is_new : 1
         * money : 0.00
         * total_money : 0.00
         * times : 0
         * unionid : 1
         */

        private String nickname;
        private String avatar;
        private String realname;
        private String alipay;
        private int uid;
        private String openid;
        private int is_new;
        private String money;
        private String total_money;
        private int times;
        private String unionid;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }

        public String getAlipay() {
            return alipay;
        }

        public void setAlipay(String alipay) {
            this.alipay = alipay;
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

        public int getIs_new() {
            return is_new;
        }

        public void setIs_new(int is_new) {
            this.is_new = is_new;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getTotal_money() {
            return total_money;
        }

        public void setTotal_money(String total_money) {
            this.total_money = total_money;
        }

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }
    }
}
