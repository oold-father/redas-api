package com.cdgeekcamp.redas.api.core;

public class RecruitingPageApiMessage {
    private String uuid;
    private String message;

    protected RecruitingPageApiMessage() {
    }

    public RecruitingPageApiMessage(String uuid, String message) {
        this.uuid = uuid;
        this.message = message;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
