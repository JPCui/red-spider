package cn.cjp.wechat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.cjp.app.model.request.BookPageRequest;
import cn.cjp.app.model.response.BookResponse;
import cn.cjp.app.service.BookService;
import cn.cjp.utils.Page;

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
			Page<BookResponse> bookPage = bookService.findAll(bookRequest);
			if (!bookPage.getResultList().isEmpty()) {
				StringBuilder sb = new StringBuilder();
				bookPage.getResultList().forEach(bookResponse -> {
					sb.append(bookResponse.getName());
					sb.append(" - ");
					sb.append(bookResponse.getAuthor());
				});
			}
		}

		return resTxt;
	}

}
