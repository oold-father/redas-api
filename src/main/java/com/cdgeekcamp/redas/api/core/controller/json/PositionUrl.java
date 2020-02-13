package com.cdgeekcamp.redas.api.core.controller.json;

public class PositionUrl {
    private String url;
    private Integer maxSize;

    public PositionUrl() {
    }

    public PositionUrl(String url, Integer maxSize) {
        this.url = url;
        this.maxSize = maxSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }
}
