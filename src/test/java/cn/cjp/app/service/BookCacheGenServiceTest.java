package cn.cjp.app.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import cn.cjp.SpiderAppApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = SpiderAppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BookCacheGenServiceTest {

    @Autowired
    BookCacheGenService bookCacheGenService;

    @Test
    public void gen() {
        bookCacheGenService.genBookFileCache();
    }
}
