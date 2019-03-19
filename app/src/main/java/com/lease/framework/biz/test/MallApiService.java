package com.lease.framework.biz.test;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MallApiService {

    @GET("/usercenter/api/v0.1/users/phones/{phoneNum}/verify-codes/{mode}")
    Observable<VerifyCodeDO> obtainVerifyCode(@Path("phoneNum") String phoneNum, @Path("mode") int mode);

}