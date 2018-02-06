package cn.cjp.app.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.cjp.app.model.request.BookPageRequest;
import cn.cjp.app.model.response.BookResponse;
import cn.cjp.app.model.response.ChaptorsResponse.ChaptorResponse;
import cn.cjp.app.model.response.Response;
import cn.cjp.app.model.response.SectionResponse;
import cn.cjp.app.model.vo.BookChaptorVO;
import cn.cjp.app.model.vo.BookSectionVO;
import cn.cjp.app.service.BookService;
import cn.cjp.app.util.ResponseUtil;

/**
 * books
 * 
 * @author sucre
 *
 */
@RequestMapping
@Controller
public class BookController {

	@Autowired
	BookService bookService;

	@RequestMapping("/books")
	public ModelAndView books(BookPageRequest bookRequest) {
		Response<?> response = Response.success(bookService.findAll(bookRequest));

		return ResponseUtil.get(response, "/book/books");
	}

	@RequestMapping("/book/{bookDocId}")
	public ModelAndView chaptors(@PathVariable String bookDocId) {
		BookResponse book = bookService.findOne(bookDocId);
		List<ChaptorResponse> chaptors = bookService.findAllChaptors(bookDocId);

		BookChaptorVO vo = new BookChaptorVO();
		vo.setBook(book);
		vo.setChaptors(chaptors);

		Response<?> response = Response.success(vo);

		return ResponseUtil.get(response, "/book/book-chaptors");
	}

	/**
	 * 
	 * @param bookDocId
	 * @param index
	 *            下标，base：1
	 * @return
	 */
	@RequestMapping("/book/{bookDocId}/{index}")
	public ModelAndView chaptors(@PathVariable String bookDocId, @PathVariable int index) {

		BookResponse book = bookService.findOne(bookDocId);

		SectionResponse section = bookService.getSectionByBookDocIdAndIndex(bookDocId, index);

		BookSectionVO vo = new BookSectionVO();
		vo.setBook(book);
		vo.setSection(section);

		Response<?> response = Response.success(vo);
		return ResponseUtil.get(response, "/book/book-sections");
	}

}
