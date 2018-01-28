package cn.cjp.app.model.request;

import lombok.Data;

@Data
public class PageRequest {

	public static final int DEFAULT_PAGE_SIZE = 10;

	private int page = 1;

}
