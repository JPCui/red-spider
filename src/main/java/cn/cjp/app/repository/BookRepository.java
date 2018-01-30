package cn.cjp.app.repository;

import org.springframework.stereotype.Repository;

import cn.cjp.app.model.doc.Book;

@Repository
public class BookRepository extends BaseRepository<Book> {

	@Override
	protected Class<Book> getTClass() {
		return Book.class;
	}

}
