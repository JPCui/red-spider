package cn.cjp.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.cjp.app.exception.ServiceException;
import cn.cjp.app.model.request.BookPageRequest;
import cn.cjp.app.model.response.BookResponse;
import cn.cjp.utils.FileUtil;
import cn.cjp.utils.Logger;
import cn.cjp.utils.Page;
import cn.cjp.utils.StringUtil;

@Service
public class BookCacheGenService {

	private static final Logger LOGGER = Logger.getLogger(BookCacheGenService.class);

	private static final String CACHE_FILE_PREFIX = "/data/cache/index_gen_";

	@Autowired
	BookService bookService;

	public void genBookFileCache() {
		final String key = "book";
		BookPageRequest bookPageRequest = new BookPageRequest();
		int page = this.getLsstCachePage(key);

		while (true) {
			LOGGER.info(String.format("gen cache: %s, %s", key, page));

			bookPageRequest.setPage(page);
			// 无需手动保存结果，由 CacheManager 管理
			Page<BookResponse> bookPage = bookService.findAll(bookPageRequest);
			if (bookPage.getResultList().isEmpty()) {
				break;
			}
			bookPage.getResultList().forEach(book -> {
				try {
					bookService.findAllChaptors(book.get_id());
					bookService.findOne(book.get_id());
				} catch (ServiceException e) {
					LOGGER.error(e.getMessage(), e);
				}
			});

			this.saveCacheGenPage(key, page);
			page++;
		}
	}

	/**
	 * 获取上次缓存执行到的页码
	 */
	private int getLsstCachePage(String key) {
		String page = FileUtil.readLine(CACHE_FILE_PREFIX + key);
		if (!StringUtil.isEmpty(page)) {
			return Integer.parseInt(page);
		}
		return 1;
	}

	/**
	 * 保存本次缓存执行的页码
	 */
	private void saveCacheGenPage(String key, int page) {
		FileUtil.write("" + page, CACHE_FILE_PREFIX + key, false);
	}

}
