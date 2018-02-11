package cn.cjp.app.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import cn.cjp.SpiderAppApplication;
import cn.cjp.app.model.doc.ReaderRecord;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = SpiderAppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ReaderRecordServiceTest {

	@Autowired
	ReaderRecordService readerRecordService;

	String userId = "";

	@Test
	public void test() throws IllegalAccessException {
		String bookDocId = "bookDocId";
		int index = 1;
		readerRecordService.save(userId, bookDocId, index);

		ReaderRecord readerRecord = readerRecordService.findLatestByUserId(userId);

		Assert.assertNotNull(readerRecord);
		Assert.assertEquals(bookDocId, readerRecord.getBookDocId());
		Assert.assertEquals(index, readerRecord.getIndex());

	}

}
