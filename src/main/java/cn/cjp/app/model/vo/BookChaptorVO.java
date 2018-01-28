package cn.cjp.app.model.vo;

import java.util.List;

import cn.cjp.app.model.response.BookResponse;
import cn.cjp.app.model.response.ChaptorsResponse.ChaptorResponse;
import lombok.Data;

@Data
public class BookChaptorVO {

	BookResponse book;

	List<ChaptorResponse> chaptors;

}
