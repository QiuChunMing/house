package com.example.house.base;
//作者：一起来看雷阵雨
//链接：https://www.zhihu.com/question/326779714/answer/700763458
//来源：知乎
//著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

import com.example.house.exception.StatusCode;
import com.example.house.exception.UnFillStackTraceException;
import org.apache.commons.lang3.StringUtils;

public class APIException extends UnFillStackTraceException {
    private static final long serialVersionUID = -1043498038361659805L;

    private final StatusCode statusCode;

    public APIException(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public APIException(StatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return this.statusCode;
    }

    @Override
    public String getMessage() {
        return StringUtils.defaultIfBlank(super.getMessage(), statusCode.defaultMessage);
    }
}