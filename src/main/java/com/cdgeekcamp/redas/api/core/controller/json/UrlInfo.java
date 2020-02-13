package com.cdgeekcamp.redas.api.core.controller.json;

public class UrlInfo {
    private String srcPosId;
    private String platform;
    private String url;

    public UrlInfo() {
    }

    public UrlInfo(String srcPosId, String platform, String url) {
        this.srcPosId = srcPosId;
        this.platform = platform;
        this.url = url;
    }

    public String getSrcPosId() {
        return srcPosId;
    }

    public void setSrcPosId(String srcPosId) {
        this.srcPosId = srcPosId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
