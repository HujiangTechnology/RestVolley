package com.android.volley;

/**
 * class description here
 *
 * @author simon
 * @version 1.0.0
 * @since 2017-08-14
 */
public class SSLError extends VolleyError {

    public SSLError(String exceptionMessage) {
        super(exceptionMessage);
    }

    public SSLError(String exceptionMessage, Throwable throwable) {
        super(exceptionMessage, throwable);
    }
}
