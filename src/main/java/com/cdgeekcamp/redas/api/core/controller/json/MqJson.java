package com.cdgeekcamp.redas.api.core.controller.json;

public class MqJson {
    private String msg;

    public MqJson(){}

    public MqJson(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
