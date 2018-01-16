package cn.cjp.spider.core.mongo;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClientURI;

public class DocumentCheckTest {

	@Test
	public void checkBookNums() throws UnknownHostException {
		Map<String, Integer> rs = new HashMap<>();

		SimpleMongoDbFactory factory = new SimpleMongoDbFactory(new MongoClientURI("mongodb://localhost:27017/test"));
		MongoTemplate mongoTemplate = new MongoTemplate(factory);

		DBCollection bookColl = mongoTemplate.getCollection("99lib-book");
		DBCollection bookChaptorsColl = mongoTemplate.getCollection("99lib-book-chaptors");
		DBCollection bookSectionsColl = mongoTemplate.getCollection("99lib-book-sections");

		long bookCount = bookColl.count();
		rs.put("book", (int) bookCount);
		System.out.println("book : " + bookCount);

		DBCursor booksIt = bookColl.find();
		booksIt.forEach(book -> {
			String bookId = book.get("book_id").toString();
			DBCursor chaptorsIt = bookChaptorsColl.find(new BasicDBObject("book_id", bookId));
			chaptorsIt.forEach(chaptor -> {
				BasicDBList chaptorIds = (BasicDBList) chaptor.get("chaptor_id");
				rs.put(String.format("book-%s", bookId), chaptorIds.size());
				System.out.println(String.format("book-%s : %s", bookId, chaptorIds.size()));
				chaptorIds.forEach(chaptorId -> {
					DBCursor sectionsIt = bookSectionsColl.find(new BasicDBObject("chaptor_id", chaptorId));
					if (sectionsIt.hasNext()) {
						BasicDBList sections = (BasicDBList) sectionsIt.next().get("content");
						rs.put(String.format("book-%s-%s", bookId, chaptorId), sections.size());
						System.out.println(String.format("book-%s-%s : %s", bookId, chaptorId, sections.size()));
					}
				});
			});
		});

		rs.keySet().forEach(key -> {
			System.out.println(key + " : " + rs.get(key));
		});

	}

}
