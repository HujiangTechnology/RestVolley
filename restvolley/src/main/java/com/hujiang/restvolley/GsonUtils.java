/*
 * GsonUtils      2015-12-10
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
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

    private static Gson sGson;

    /**
     * get Gson.
     * @return Gson.
     */
    public static Gson getGson() {
        if (sGson == null) {
            sGson = newGson();
        }

        return sGson;
    }

    /**
     * new Gson.
     * @return Gson.
     */
    public static Gson newGson() {
        return new GsonBuilder().registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
            @Override
            public JsonElement serialize(Double aDouble, Type type, JsonSerializationContext jsonSerializationContext) {
                if (aDouble == aDouble.longValue()) {
                    return new JsonPrimitive(aDouble.longValue());
                }
                return new JsonPrimitive(aDouble);
            }
        }).create();
    }

    /**
     * convert json String to Object.
     * @param jsonString json string
     * @param clazz Object class
     * @param <T> Object Class Type
     * @return Object
     */
    public static <T> T fromJsonStringThrowEx(String jsonString, Class<T> clazz) throws JsonSyntaxException {
        return getGson().fromJson(jsonString, clazz);
    }

    @Deprecated
    public static <T> T fromJsonString(String jsonString, Class<T> clazz) {
        return optFromJsonString(jsonString, clazz);
    }

    public static <T> T optFromJsonString(String jsonString, Class<T> clazz) {
        try {
            return getGson().fromJson(jsonString, clazz);
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
    public static <T> T fromJsonStringThrowEx(String json, Type typeOfT) throws JsonSyntaxException {
        return getGson().fromJson(json, typeOfT);
    }

    /**
     * convert json string to the Object of the specified Type.
     * @param json json string
     * @param typeOfT Type
     * @param <T> Type
     * @return Object of T
     */
    @Deprecated
    public static <T> T fromJsonString(String json, Type typeOfT) {
        return optFromJsonString(json, typeOfT);
    }

    public static <T> T optFromJsonString(String json, Type typeOfT) {
        try {
            return getGson().fromJson(json, typeOfT);
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
    public static String toJsonStringThrowEx(Object object) throws Exception  {
        return getGson().toJson(object);
    }

    /**
     * vonvert Object to json string.
     * @param object Object
     * @return json string.
     */
    @Deprecated
    public static String toJsonString(Object object) {
        return optToJsonString(object);
    }

    public static String optToJsonString(Object object) {
        try {
            return getGson().toJson(object);
        } catch (Throwable var2) {
            var2.printStackTrace();
            return "";
        }
    }
}