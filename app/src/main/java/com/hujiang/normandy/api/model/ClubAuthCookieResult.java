package com.hujiang.normandy.api.model;

import com.google.gson.annotations.SerializedName;
import com.hujiang.normandy.api.BaseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Conquer.
 */
public class ClubAuthCookieResult extends BaseModel {

    @SerializedName("data")
    private CookieInfo mCookieInfo = new CookieInfo();

    public CookieInfo getCookieInfo() {
        return mCookieInfo;
    }

    public void setCookieInfo(CookieInfo cookieInfo) {
        mCookieInfo = cookieInfo;
    }

    public class CookieInfo implements Serializable {

        @SerializedName("cookie_domains")
        private List<String> mCookieDomains;

        @SerializedName("cookie_name")
        private String mCookieName;

        @SerializedName("cookie_value")
        private String mCookieValue;

        public List<String> getCookieDomains() {
            return mCookieDomains;
        }

        public void setCookieDomains(List<String> cookieDomains) {
            mCookieDomains = cookieDomains;
        }

        public String getCookieName() {
            return mCookieName;
        }

        public void setCookieName(String cookieName) {
            mCookieName = cookieName;
        }

        public String getCookieValue() {
            return mCookieValue;
        }

        public void setCookieValue(String cookieValue) {
            mCookieValue = cookieValue;
        }
    }
}
