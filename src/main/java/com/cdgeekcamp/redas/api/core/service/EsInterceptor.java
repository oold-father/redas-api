package com.cdgeekcamp.redas.api.core.service;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EsInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String position = request.getParameter("position");
        if (position == null){
            System.out.println("no position");
        }else {
            String[] parts = position.split("/");
            request.setAttribute("position", parts);
        }

        return true;// 只有返回true才会继续向下执行，返回false取消当前请求

    }
}