package cn.cjp.app.model.response;

import lombok.Data;

@Data
public class SectionResponse {

	private String _id;
	
	private String[] content;

	private PageIndexResponse curr;

	private PageIndexResponse prev;

	private PageIndexResponse next;

	/**
	 * 用來跳轉，上一章、下一章
	 * 
	 * @author sucre
	 *
	 */
	@Data
	public static class PageIndexResponse {

		public static PageIndexResponse build(int index, String message) {
			PageIndexResponse response = new PageIndexResponse();
			response.setIndex(index);
			response.setMessage(message);
			return response;
		}

		/**
		 * 下標/頁碼
		 */
		private int index = 0;

		/**
		 * 信息
		 */
		private String message = "";

	}

}
