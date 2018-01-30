package cn.cjp.app.model.doc;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = "99lib-book-chaptors")
@Data
@EqualsAndHashCode(callSuper = false)
public class Chaptors extends BaseDoc {

	private String titleName;

	@Field("book_id")
	private String bookId;

	private Chaptor[] chaptors;

	@Data
	public static class Chaptor {
		@Field("chaptor_id")
		private String chaptorId;
		
		@Field("chaptor_name")
		private String chaptorName;
	}
}
