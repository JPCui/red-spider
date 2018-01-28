package cn.cjp.app.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BookPageRequest extends PageRequest {

	private String name;

	private String type;

	private String author;

	private String[] tags;

}
