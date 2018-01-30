package cn.cjp.app.model.vo;

import cn.cjp.app.model.response.BookResponse;
import cn.cjp.app.model.response.SectionResponse;
import lombok.Data;

@Data
public class BookSectionVO {

	BookResponse book;

	SectionResponse section;

}
