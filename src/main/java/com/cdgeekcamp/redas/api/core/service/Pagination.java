package com.cdgeekcamp.redas.api.core.service;

public class Pagination {
    public Integer Page(Integer page){
        if(page == null || page <= 0){
            page = 1;
        }else {
            page = page-1;
        }
        return page;
    }
}
