package com.example.house.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ResultResponseHandler implements ResponseBodyAdvice<Object> {
    public static final String RESPONSE_RESULT_ANN = "RESPONSE-RESULT-ANN";

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        ResultResponse resultResponse = (ResultResponse) request.getAttribute(RESPONSE_RESULT_ANN);
        return resultResponse != null;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType
            , Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest
            , ServerHttpResponse serverHttpResponse) {
        log.info("进入返回体重写格式");
        //将统一异常处理包装的结果再包装
        if (o instanceof ResponseEntity) {
            ResponseEntity o1 = (ResponseEntity) o;
            if (o1.getBody() instanceof ErrorBody) {
                return o1;
            }
        }
        return ApiResponse.success(o);
    }
}
