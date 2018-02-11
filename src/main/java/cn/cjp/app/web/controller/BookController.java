package cn.cjp.app.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import cn.cjp.app.exception.ServiceAssert;
import cn.cjp.app.model.doc.ReaderRecord;
import cn.cjp.app.model.request.BookPageRequest;
import cn.cjp.app.model.request.LoginUser;
import cn.cjp.app.model.response.BookResponse;
import cn.cjp.app.model.response.ChaptorsResponse.ChaptorResponse;
import cn.cjp.app.model.response.Response;
import cn.cjp.app.model.response.SectionResponse;
import cn.cjp.app.model.vo.BookChaptorVO;
import cn.cjp.app.model.vo.BookSectionVO;
import cn.cjp.app.service.BookService;
import cn.cjp.app.service.ReaderRecordService;
import cn.cjp.app.util.ResponseUtil;
import cn.cjp.utils.Logger;
import io.swagger.annotations.Api;

/**
 * books
 *
 * @author sucre
 */
@RequestMapping
@Controller
@Api
public class BookController extends BaseController {

    private static final Logger LOGGER = Logger.getLogger(BookController.class);

    @Autowired
    private BookService bookService;

    @Autowired
    private ReaderRecordService readerService;

    @RequestMapping("/books")
    public ModelAndView books(BookPageRequest bookRequest) {
        Response<?> response = Response.success(bookService.findAll(bookRequest));

        return ResponseUtil.get(response, "/book/books");
    }

    /**
     * 获取所有章节
     */
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
     * @param index 下标，base：1
     */
    @RequestMapping("/book/{bookDocId}/{index}")
    public ModelAndView chaptors(@PathVariable String bookDocId, @PathVariable int index) throws IllegalAccessException {

        BookResponse book = bookService.findOne(bookDocId);

        SectionResponse section = bookService.getSectionByBookDocIdAndIndex(bookDocId, index);

        {
            // 如果已登录，记录阅读状态
            LoginUser loginUser = this.getCurrentUser();
            if (loginUser != null) {
                String userId = loginUser.getUserId();
                readerService.save(userId, bookDocId, section.getCurr().getIndex());
            } else {
                LOGGER.info(String.format("anonymous user reading: %s, %s", bookDocId, index));
                readerService.save("", bookDocId, section.getCurr().getIndex());
            }
        }

        BookSectionVO vo = new BookSectionVO();
        vo.setBook(book);
        vo.setSection(section);

        Response<?> response = Response.success(vo);
        return ResponseUtil.get(response, "/book/book-sections");
    }

    /**
     * 获取上次记录
     *
     * @param userId userId
     * @return #chaptors
     */
    @GetMapping("/book/latest")
    public ModelAndView getLastRead(String userId) throws IllegalAccessException {
        ReaderRecord readerRecord = readerService.findLatestByUserId(userId);
        ServiceAssert.assert404(readerRecord);
        return this.chaptors(readerRecord.getBookDocId(), readerRecord.getIndex());
    }

}
