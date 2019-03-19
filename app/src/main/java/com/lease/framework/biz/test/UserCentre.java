package com.lease.framework.biz.test;

public class UserCentre {

    private static UserCentre sInstance;

    private UserCentre() {}

    private UserDO userDO;

    public static UserCentre getInstance() {
        if(null == sInstance) {
            synchronized (UserCentre.class) {
                if(null == sInstance) {
                    sInstance = new UserCentre();
                }
            }
        }

        return sInstance;
    }

    public UserDO getUserDO() {
        return userDO;
    }

    public void setUserDO(UserDO userDO) {
        this.userDO = userDO;
    }
}
