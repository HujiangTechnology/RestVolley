/*
 * GsonUtils      2015-12-10
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * GsonUtils.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-12-10
 */
public class GsonUtils {

    private static Gson sGson = new GsonBuilder().create();

    /**
     * get Gson.
     * @return Gson.
     */
    public static Gson getGson() {
        return sGson;
    }

    /**
     * new Gson.
     * @return Gson.
     */
    public static Gson newGson() {
        return new GsonBuilder().create();
    }

    /**
     * convert json String to Object.
     * @param jsonString json string
     * @param clazz Object class
     * @param <T> Object Class Type
     * @return Object
     */
    public static <T> T fromJsonString(String jsonString, Class<T> clazz) {
        try {
            return sGson.fromJson(jsonString, clazz);
        } catch (Throwable var3) {
            var3.printStackTrace();
            return null;
        }
    }

    /**
     * convert json string to the Object of the specified Type.
     * @param json json string
     * @param typeOfT Type
     * @param <T> Type
     * @return Object of T
     */
    public static <T> T fromJsonString(String json, Type typeOfT) {
        try {
            return sGson.fromJson(json, typeOfT);
        } catch (JsonSyntaxException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    /**
     * vonvert Object to json string.
     * @param object Object
     * @return json string.
     */
    public static String toJsonString(Object object) {
        try {
            return sGson.toJson(object);
        } catch (Throwable var2) {
            var2.printStackTrace();
            return "";
        }
    }
}