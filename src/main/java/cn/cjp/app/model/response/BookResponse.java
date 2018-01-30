package cn.cjp.app.model.response;

import lombok.Data;

@Data
public class BookResponse {

	private String _id;

	private String name;

	private String author;
	
	private String type;
	
	private String[] tags;

	private String summary;

}
