package cn.cjp.app.model.response;

import java.util.List;

import lombok.Data;

@Data
public class ChaptorsResponse {

	private String _id;

	List<ChaptorResponse> chaptors;

	/**
	 * 抓取的ID不能暴露
	 * 
	 * @author sucre
	 */
	@Data
	public static class ChaptorResponse {

		private String chaptorName;

		private String viewId;
	}
}
