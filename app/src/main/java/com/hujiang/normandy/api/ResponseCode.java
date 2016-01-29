/*
 * AccountResponseCode		2015-04-15
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */
package com.hujiang.normandy.api;

import com.hujiang.framework.api.model.APIResponseCode;

/**
 * class description here
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-04-15
 */
public interface ResponseCode extends APIResponseCode {

    public static final int CODE_ACCESS_DENY = -8193;
    public static final int CODE_EXCEED_QUOTA = -8194;
    public static final int CODE_ANONYMOUS_USER_DISALLOWED = -8196;
    public static final int CODE_NO_ACTIVE = -8197;

    //20016

    public static final int CODE_NO_ACCOUNT = 10000;
    public static final int CODE_ACCOUNT_PASSWORD_MISMATCH = 10001;
    public static final int CODE_ACCOUNT_LOCKED = 10003;
    public static final int CODE_SMS_CODE_VERIFY_ERROR = 10004;
    public static final int CODE_ACCOUNT_EMPTY = 10005;
    public static final int CODE_PASSWORD_EMPTY = 10006;
    public static final int CODE_SMS_CODE_EMPTY = 10007;
    public static final int CODE_SMS_CODE_LOGIN_ERROR = 10008;
    public static final int CODE_USERNAME_EXIST = 20000;
    public static final int CODE_USERNAME_REGIST_FORBID = 20001;
    public static final int CODE_LENGTH_ERROR_OF_USERNAME = 20002;
    public static final int CODE_INVALID_CHAR_IN_USERNAME = 20003;
    public static final int CODE_EMAIL_EMPTY = 20004;
    public static final int CODE_EMAIL_FORMAT_ERROR = 20005;
    public static final int CODE_EMAIL_FORBID = 20006;
    public static final int CODE_EMAIL_HAS_EXIST = 20007;
    public static final int CODE_USERNAME_FORMAT_ERROR = 20008;
    public static final int CODE_USERNAME_LENGTH_ERROR = 20016;
    public static final int CODE_NICKNAME_EMPTY = 20100;
    public static final int CODE_NICKNAME_TOO_SHORT = 20101;
    public static final int CODE_NICKNAME_TOO_LONG = 20102;
    public static final int CODE_NICKNAME_SENSITIVE = 20103;
    public static final int CODE_PHONE_ERROR = 30000;
    public static final int CODE_PHONE_EXIST = 30001;
    public static final int CODE_SMS_BUSY = 30002;
    public static final int CODE_SMS_OVERLOAD = 30003;
    public static final int CODE_SMS_SEND_ERROR = 30004;
    public static final int CODE_PASS_ERROR = 40000;
    public static final int CODE_PASS_WEAK = 40001;
    public static final int CODE_PASS_SMS_EMPTY = 40002;
    public static final int CODE_PASS_SMS_ERRO = 40003;
    public static final int CODE_PASS_TOKEN_ERROR = 40004;
    public static final int CODE_SMS_CODE_ERROR = 40005;
    public static final int CODE_SMS_CODE_TIMEOUT = 40006;
    public static final int CODE_SMS_CODE_ERROR_OVERLOAD = 40007;
    public static final int CODE_DEVICEID_MISS = 41200;
    public static final int CODE_INVALID_ACCESS_TOKEN = 50000;
    public static final int CODE_INVALID_APPKEY = 50001;
    public static final int CODE_INVALID_APPSIGN = 50002;
    public static final int CODE_RESET_PASS_EMAIL_OVERLOAD = 50003;
    public static final int CODE_INVALID_REFRESH_TOKEN = 50100;
    public static final int CODE_INVALID_REFRESH_TOKEN_APPID = 50101;
    public static final int CODE_INVALID_UNIONID_APPID_DEVICEID = 50201;
    public static final int CODE_INVALID_SCOPE = 50300;
    public static final int CODE_CCTOKEN_TIMEOUT = 61000;
    public static final int CODE_DEVICEID_UUID_NOT_MATCH = 61001;
    public static final int CODE_INVALID_REQUEST = 61002;
}
