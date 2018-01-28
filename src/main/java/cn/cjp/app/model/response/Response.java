package cn.cjp.app.model.response;

import lombok.Data;

@Data
public class Response<T> {

	private static final int SUCCESS = 200;

	private static final int FAIL = 500;

	private int code;
	private T data;
	private String message;

	public static <T> Response<T> success(T data) {
		Response<T> response = new Response<>();
		response.setCode(SUCCESS);
		response.setData(data);
		return response;
	}

	public static Response<?> fail(String message) {
		Response<?> response = new Response<>();
		response.setCode(FAIL);
		response.setMessage(message);
		return response;
	}

}
