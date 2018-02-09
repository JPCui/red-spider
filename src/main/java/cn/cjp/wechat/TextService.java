package cn.cjp.wechat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.cjp.app.model.request.BookPageRequest;
import cn.cjp.app.model.response.BookResponse;
import cn.cjp.app.service.BookService;

@Service

public class TextService {

	@Autowired
	BookService bookService;

	/**
	 * @param txt
	 * @return
	 */
	public String chat(String txt) {

		String resTxt = null;

		if (resTxt == null) {
			BookPageRequest bookRequest = new BookPageRequest();
			bookRequest.setAuthor(txt);
			List<BookResponse> bookResponses = bookService.findAll(bookRequest);
			if (!bookResponses.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				bookResponses.forEach(bookResponse -> {
					sb.append(bookResponse.getName());
					sb.append(" - ");
					sb.append(bookResponse.getAuthor());
				});
			}
		}

		return resTxt;
	}

}
