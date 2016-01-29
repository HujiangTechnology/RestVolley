/*
 * Task      2015-11-14
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley;

/**
 * Task.
 * @param <IT> 输入参数类型
 * @param <OT> 输出参数类型
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-11-14
 */
public abstract class Task<IT, OT> {

    IT mInput;

    /**
     * 以输入参数构造一个Task.
     * @param input 输入参数
     */
    public Task(IT input) {
        mInput = input;
    }

    /**
     * 后台任务执行回调.
     * @param param 输入参数
     * @return 返回结果
     */
    protected abstract OT onDoInBackground(IT param);

    /**
     * 前台任务执行回调.
     * @param result 输入参数
     */
    protected abstract void onPostExecuteForeground(OT result);
}