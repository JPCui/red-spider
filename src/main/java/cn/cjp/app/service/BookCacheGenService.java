package cn.cjp.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import cn.cjp.app.model.request.BookPageRequest;
import cn.cjp.app.model.response.BookResponse;
import cn.cjp.core.cache.Cache;
import cn.cjp.utils.FileUtil;
import cn.cjp.utils.Logger;
import cn.cjp.utils.StringUtil;

@Service
public class BookCacheGenService {

    private static final Logger LOGGER = Logger.getLogger(BookCacheGenService.class);

    private static final String CACHE_FILE_PREFIX = "/data/cache/index_gen_";

    @Autowired
    BookService bookService;

    @Autowired
    private Cache fileCache;

    public void genBookFileCache() {
        final String key = "book";
        BookPageRequest bookPageRequest = new BookPageRequest();
        int page = this.getLsstCachePage(key);

        while (true) {
            LOGGER.info(String.format("gen cache: %s, %s", key, page));

            bookPageRequest.setPage(page);
            // 无需手动保存结果，由 CacheManager 管理
            List<BookResponse> books = bookService.findAll(bookPageRequest);
            if (books.isEmpty()) {
                break;
            }
            books.forEach(book -> {
                bookService.findAllChaptors(book.get_id());
                bookService.findOne(book.get_id());
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
