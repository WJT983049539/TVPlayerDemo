package com.chinamobile.tvplayerdemo.model;

import java.io.Serializable;

/**
 * 添加节目格式
 */
public class playUrlbean implements Serializable {
    private String playurl;
    private String contentId;

    public String getPlayurl() {
        return playurl;
    }

    public void setPlayurl(String playurl) {
        this.playurl = playurl;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }
}
