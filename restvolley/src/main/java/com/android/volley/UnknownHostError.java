package com.android.volley;

/**
 * class description here
 *
 * @author simon
 * @version 1.0.0
 * @since 2017-08-14
 */
public class UnknownHostError extends VolleyError {

    public UnknownHostError(String exceptionMessage) {
        super(exceptionMessage);
    }

    public UnknownHostError(String exceptionMessage, Throwable throwable) {
        super(exceptionMessage, throwable);
    }
}
