package com.cdgeekcamp.redas.api.core.controller.json;

public class PositionDetailHtml {
    private String url;
    private String platform;
    private String htmlString;
    private String spiderUuid;

    public PositionDetailHtml() {
    }

    public PositionDetailHtml(String url, String platform, String htmlString, String spiderUuid) {
        this.url = url;
        this.platform = platform;
        this.htmlString = htmlString;
        this.spiderUuid = spiderUuid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getHtmlString() {
        return htmlString;
    }

    public void setHtmlString(String htmlString) {
        this.htmlString = htmlString;
    }

    public String getSpiderUuid() {
        return spiderUuid;
    }

    public void setSpiderUuid(String spiderUuid) {
        this.spiderUuid = spiderUuid;
    }
}
