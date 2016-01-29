/*
 * FileWrapper      2015-12-16
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.webapi.request;

import java.io.File;
import java.io.Serializable;

/**
 * file wrapper, contains file, file content type, custom file name.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-12-16
 */
public class FileWrapper implements Serializable {
    /**
     * file.
     */
    public final File file;
    /**
     * file content type.
     */
    public final String contentType;
    /**
     * custom file name.
     */
    public final String customFileName;

    /**
     * constructor.
     * @param file file
     * @param contentType file content type
     * @param customFileName custom file name
     */
    public FileWrapper(File file, String contentType, String customFileName) {
        this.file = file;
        this.contentType = contentType;
        this.customFileName = customFileName;
    }
}
