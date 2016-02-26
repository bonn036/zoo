package com.mmnn.bonn036.zoo.network.account;

import com.mmnn.bonn036.zoo.network.BaseResponse;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LoginInfo extends BaseResponse {

    public UserInfo data;

    public static class UserInfo implements Serializable {
        public String id;/* id*/
        public String nickName;/* 昵称*/
        public String height;/* 身高（cm）*/
        public String weight;/* 体重（kg）*/
        public String yob;/* 出生年*/
        public String introduce;/* 个人简介*/
        public String ico; /* 头像*/
        public String token;
    }
}
