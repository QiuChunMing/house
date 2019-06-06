package com.example.house.base;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class ResultResponseInterceptor implements HandlerInterceptor {
    public static final String RESPONSE_RESULT_ANN = "RESPONSE-RESULT-ANN";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            Class<?> beanType = handlerMethod.getBeanType();
            Method method = handlerMethod.getMethod();
            //判断是否在类上加了注解
            if (beanType.isAnnotationPresent(ResultResponse.class)) {
                //设置请求体，在ResponseAdvice接口进行判断
                request.setAttribute(RESPONSE_RESULT_ANN, beanType.getAnnotation(ResultResponse.class));
            } else if (method.isAnnotationPresent(ResultResponse.class)) {
                //设置请求体，在ResponseAdvice接口进行判断
                request.setAttribute(RESPONSE_RESULT_ANN, method.getAnnotation(ResultResponse.class));
            }
        }
        return true;
    }
}
