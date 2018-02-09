package cn.cjp.app.model.doc;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = "99lib-book-sections")
@Data
@EqualsAndHashCode(callSuper = false)
public class Sections extends BaseSpiderDoc {

	@Field("section_id")
	private String sectionId;

	@Field("chaptor_id")
	private String chaptorId;

	private String[] content;

}
