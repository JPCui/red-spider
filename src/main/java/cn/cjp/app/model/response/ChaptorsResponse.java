package cn.cjp.app.model.response;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

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

		@Field("chaptor_name")
		private String chaptorName;
	}
}
