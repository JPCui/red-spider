package cn.cjp.app.web.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import cn.cjp.app.exception.ServiceAssert;
import cn.cjp.app.model.doc.Book;
import cn.cjp.app.model.doc.Chaptors;
import cn.cjp.app.model.doc.Chaptors.Chaptor;
import cn.cjp.app.model.doc.Sections;
import cn.cjp.app.model.request.BookPageRequest;
import cn.cjp.app.model.request.PageRequest;
import cn.cjp.app.model.response.BookResponse;
import cn.cjp.app.model.response.ChaptorsResponse.ChaptorResponse;
import cn.cjp.app.model.response.SectionResponse;
import cn.cjp.app.model.response.SectionResponse.PageIndexResponse;
import cn.cjp.app.repository.BookRepository;
import cn.cjp.app.repository.ChaptorsRepository;
import cn.cjp.app.repository.SectionsRepository;
import cn.cjp.utils.StringUtil;

@Service
public class BookService {

	public static final String COLLECTION_BOOK = "99lib-book";

	public static final String COLLECTION_BOOK_CHAPTORS = "99lib-book-chaptors";

	public static final String COLLECTION_BOOK_SECTIONS = "99lib-book-sections";

	@Autowired
	BookRepository bookRepository;

	@Autowired
	ChaptorsRepository chaptorsRepository;

	@Autowired
	SectionsRepository sectionsRepository;

	public List<BookResponse> findAll(BookPageRequest bookRequest) {

		JSONObject requestJson = (JSONObject) JSONObject.toJSON(bookRequest);

		int page = (int) requestJson.remove("page");
		int pageSize = PageRequest.DEFAULT_PAGE_SIZE;

		Criteria criteria = new Criteria();
		requestJson.keySet().forEach(key -> {
			if (requestJson.get(key) != null && !StringUtil.isEmpty(requestJson.get(key).toString())) {
				criteria.and(key).is(requestJson.get(key));
			}
		});

		Query query = new Query(criteria).skip((page - 1) * pageSize).limit(pageSize);

		List<Book> books = bookRepository.find(query);
		return books.stream().map(book -> {
			BookResponse bookResponse = new BookResponse();
			BeanUtils.copyProperties(book, bookResponse);
			return bookResponse;
		}).collect(Collectors.toList());
	}

	public static final String FIELD_BOOK_ID = "book_id";

	public List<ChaptorResponse> findAllChaptors(String docId) {
		Book book = bookRepository.findById(docId);
		String bookId = book.getBookId();

		Criteria criteria = new Criteria();
		criteria.and("book_id").is(bookId);
		Query query = new Query(criteria);

		Chaptors chaptors = chaptorsRepository.findOne(query);
		Chaptor[] chaptorArray = chaptors.getChaptors();

		return Arrays.asList(chaptorArray).stream().map(cha -> {
			ChaptorResponse chaptorResponse = new ChaptorResponse();
			BeanUtils.copyProperties(cha, chaptorResponse);
			return chaptorResponse;
		}).collect(Collectors.toList());

	}

	/**
	 * 根据bookDocId获取chaptor，再跟进 index 获取 section_id
	 * 
	 * @param bookDocId
	 * @param index
	 *            下标，base: 1
	 * @return
	 */
	public SectionResponse getSectionByBookDocIdAndIndex(String bookDocId, int index) {
		Book book = bookRepository.findById(bookDocId);

		Query query = new Query(Criteria.where("book_id").is(book.getBookId()));
		Chaptors chaptors = chaptorsRepository.findOne(query);
		ServiceAssert.assert404(chaptors);

		Chaptor[] chaptorArray = chaptors.getChaptors();
		int size = chaptorArray.length;
		if (index <= size) {
			Chaptor selectedChaptor = chaptorArray[index - 1];
			String chaptorId = selectedChaptor.getChaptorId();

			Criteria criteria = new Criteria();
			criteria.and("chaptor_id").is(chaptorId);
			query = new Query(criteria);
			Sections sections = sectionsRepository.findOne(query);
			ServiceAssert.assert404(sections);

			SectionResponse sectionResponse = new SectionResponse();
			BeanUtils.copyProperties(sections, sectionResponse);
			if (index > 1) {
				// prev
				sectionResponse.setPrev(PageIndexResponse.build(index - 1, chaptorArray[index - 1].getChaptorName()));
			}
			if (index < size) {
				// next
				sectionResponse.setNext(PageIndexResponse.build(index + 1, chaptorArray[index + 1].getChaptorName()));
			}
			return sectionResponse;
		}
		return null;
	}

	public BookResponse findOne(String bookId) {
		Book book = bookRepository.findById(bookId);
		BookResponse bookResponse = new BookResponse();
		BeanUtils.copyProperties(book, bookResponse);
		return bookResponse;
	}

}
