package cn.cjp.app.model.doc;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = "99lib-book")
@Data
@EqualsAndHashCode(callSuper = false)
public class Book extends BaseDoc {

	@Field("book_id")
	private String bookId;

	private String name;

	private String author;

	private String type;

	private String[] tags;

	private String summary;
}
