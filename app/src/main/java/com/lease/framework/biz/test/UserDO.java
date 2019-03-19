package com.lease.framework.biz.test;


import java.io.Serializable;

/**
 * Created by hxd on 16/9/29.
 */
public class UserDO implements Serializable {

    private String phoneNum;

    private long userId;

    private String token;

    private int type;

    private int isLogin;

    private long latestLoginTime;

    /**
     *  0,1未在黑名单 2:黑名单
     */
    private int creditFlag;
    /**
     * 0:未认证 1:成功，2:失败
     */
    private int certificateHuman;
    /**
     * 0:未认证 1:成功，2:失败
     */
    private int certificateIdCard;

    private String avatarUrl;

    private String nickName;

    private String realName;

    private String identityCard;

    /**
     * 是否新用户
     */
    private boolean newUser;

    private String employeeName;

    private int zhimaAuth; // 是否已实人认证 0 初始  1 已通过  2 认证失败

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 0 普通用户 1 虚拟用户 2 第三方登录
     * @return
     */
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 是否已登录
     * @return
     */
    public int getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(int isLogin) {
        this.isLogin = isLogin;
    }

    /**
     * 最后登录时间
     * @return
     */
    public long getLatestLoginTime() {
        return latestLoginTime;
    }

    public void setLatestLoginTime(long latestLoginTime) {
        this.latestLoginTime = latestLoginTime;
    }

    /**
     * 0,1未在黑名单 2:黑名单
     * @return
     */
    public int getCreditFlag() {
        return creditFlag;
    }

    public void setCreditFlag(int creditFlag) {
        this.creditFlag = creditFlag;
    }

    /**
     * 实人认证：0:未认证 1:成功，2:失败
     * @return
     */
    public int getCertificateHuman() {
        return certificateHuman;
    }

    public void setCertificateHuman(int certificateHuman) {
        this.certificateHuman = certificateHuman;
    }

    /**
     * 实名：0:未认证 1:成功，2:失败
     * @return
     */
    public int getCertificateIdCard() {
        return certificateIdCard;
    }

    public void setCertificateIdCard(int certificateIdCard) {
        this.certificateIdCard = certificateIdCard;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public int getZhimaAuth() {
        return zhimaAuth;
    }

    public void setZhimaAuth(int zhimaAuth) {
        this.zhimaAuth = zhimaAuth;
    }
}
