/*
 * MD5Utils      2015-12-29
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5 utils.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-12-29
 */
public class MD5Utils {

    private static final int HEX_FF = 0xFF;

    /**
     *
     * @param key for disk.
     * @return hash key.
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(HEX_FF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}