package cn.cjp.spider.exception;

public class ServiceException extends RuntimeException {

    private int code;

    public ServiceException() {
        super();
    }

    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException(int code, String msg) {
        super(msg);
        this.code = code;
    }

}
