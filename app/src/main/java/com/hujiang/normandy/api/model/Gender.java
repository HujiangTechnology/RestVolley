/*
 * GENDER      2015-07-02
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */
package com.hujiang.normandy.api.model;

/**
 * class description here
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-07-02
 */
public enum Gender {
    /**
     * 保密，未知性别
     */
    UNKNOWN(0),
    /**
     * 男性
     */
    MALE(1),
    /**
     * 女性
     */
    FEMALE(2);

    private int mValue;

    private Gender(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }
}