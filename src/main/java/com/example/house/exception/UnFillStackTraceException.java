package com.example.house.exception;
//作者：一起来看雷阵雨
//链接：https://www.zhihu.com/question/326779714/answer/700763458
//来源：知乎
//著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

public class UnFillStackTraceException extends RuntimeException {
    private static final long serialVersionUID = -3181827538683088424L;

    public UnFillStackTraceException() {
        this(null, null);
    }

    public UnFillStackTraceException(String message) {
        this(message, null);
    }

    public UnFillStackTraceException(Throwable cause) {
        this(null, cause);
    }

    public UnFillStackTraceException(String message, Throwable cause) {
        super(message, cause, false, false);
    }
}