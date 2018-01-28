package cn.cjp.app.exception;

public class ServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9174949369786032837L;

	private int code;

	public ServiceException(int code, String message) {
		super(message);
		this.code = code;
	}

	public ServiceException(int code) {
		super();
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
