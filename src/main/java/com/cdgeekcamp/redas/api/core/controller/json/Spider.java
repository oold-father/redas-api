package com.cdgeekcamp.redas.api.core.controller.json;

public class Spider {
    private Integer id;
    private String uuid;
    private String location;
    private String describe;
    private Boolean state;

    public Spider() {
    }

    public Spider(Integer id, String uuid, String location, String describe, Boolean state) {
        this.id = id;
        this.uuid = uuid;
        this.location = location;
        this.describe = describe;
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }
}
