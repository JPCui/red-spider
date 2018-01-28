package cn.cjp.app.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.cjp.app.model.request.BookPageRequest;
import cn.cjp.app.model.response.BookResponse;
import cn.cjp.app.model.response.ChaptorsResponse.ChaptorResponse;
import cn.cjp.app.model.response.Response;
import cn.cjp.app.model.response.SectionResponse;
import cn.cjp.app.web.service.BookService;

/**
 * books
 * 
 * @author sucre
 *
 */
@RequestMapping
@RestController
public class BookController {

	@Autowired
	BookService bookService;

	@RequestMapping("books")
	public Response<List<BookResponse>> books(BookPageRequest bookRequest) {
		return Response.success(bookService.findAll(bookRequest));
	}

	@RequestMapping("/book/{bookId}")
	public Response<List<ChaptorResponse>> chaptors(@PathVariable String bookId) {
		List<ChaptorResponse> chaptors = bookService.findAllChaptors(bookId);
		return Response.success(chaptors);
	}

	@RequestMapping("/book/{bookDocId}/{index}")
	public Response<SectionResponse> chaptors(@PathVariable String bookDocId, @PathVariable int index) {
		SectionResponse chaptor = bookService.getSectionByDocIdAndIndex(bookDocId, index);
		return Response.success(chaptor);
	}

}
