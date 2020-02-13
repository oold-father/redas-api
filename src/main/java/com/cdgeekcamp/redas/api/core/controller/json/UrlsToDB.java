package com.cdgeekcamp.redas.api.core.controller.json;

import java.util.ArrayList;

public class UrlsToDB {
    private String sourceUrl;
    private ArrayList<UrlInfo> urls;

    public UrlsToDB(){}

    public UrlsToDB(String sourceUrl, ArrayList<UrlInfo> urls) {
        this.sourceUrl = sourceUrl;
        this.urls = urls;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public ArrayList<UrlInfo> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<UrlInfo> urls) {
        this.urls = urls;
    }
}
