package com.example.house.base;
//作者：一起来看雷阵雨
//链接：https://www.zhihu.com/question/326779714/answer/700763458
//来源：知乎
//著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

import com.example.house.exception.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErrorBody> handleBusinessException(APIException apiException) {
        return ResponseEntity.status(apiException.getStatusCode().getHttpStatusCode())
                .body(new ErrorBody(apiException));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorBody handleUnknownException(Exception e) {
        log.error("服务器未知异常", e);
        return new ErrorBody(StatusCode.ERROR);
    }

}