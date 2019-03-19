package com.lease.framework.biz.test;

import java.io.Serializable;

public class VerifyCodeDO implements Serializable {

    private String phoneNum;

    private String securityCode;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }
}
