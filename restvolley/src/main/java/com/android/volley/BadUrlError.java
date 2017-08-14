package com.android.volley;

/**
 * class description here
 *
 * @author simon
 * @version 1.0.0
 * @since 2017-08-14
 */
public class BadUrlError extends VolleyError {

    public BadUrlError(String exceptionMessage) {
        super(exceptionMessage);
    }

    public BadUrlError(String exceptionMessage, Throwable throwable) {
        super(exceptionMessage, throwable);
    }
}
