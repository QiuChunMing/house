package com.example.house.exception;
//作者：一起来看雷阵雨
//链接：https://www.zhihu.com/question/326779714/answer/700763458
//来源：知乎
//著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

public enum StatusCode {

    /**
     * 服务器未知异常
     */
    ERROR(500000, "服务器异常"),

    //授权异常
    DISABLE_ACCOUNT(401001, "账户已被冻结"),
    INVALID_TOKEN(401002, "无效的身份凭证"),
    EXPIRED_TOKEN(401003, "身份凭证已过期"),
    NO_PERMISSION(401004, "无权限进行该操作"),
    BAD_CREDENTIALS(401005, "密码错误"),

    ILLEGAL_OPERATION(400001, "非法操作"),

    NOT_FOUND(404000,"访问的资源不存在"),

    INVALID_PARAM(422001, "参数无效");


    public final int code;
    public final String defaultMessage;

    StatusCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public int getHttpStatusCode(){
        return convertToHttpStatus(this);
    }

    public static StatusCode valueOf(int code) {
        for (StatusCode value : StatusCode.values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合'" + code + "'的StatusCode");
    }

    public static int convertToHttpStatus(StatusCode statusCode) {
        return statusCode.code / 1000;
    }

    public static int convertToHttpStatus(int code) {
        return convertToHttpStatus(valueOf(code));
    }
}