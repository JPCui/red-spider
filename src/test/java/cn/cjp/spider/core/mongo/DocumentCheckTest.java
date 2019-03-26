package cn.cjp.spider.core.mongo;

import com.alibaba.fastjson.JSON;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

public class DocumentCheckTest {

    @Test
    public void checkBookNums() {
        Map<String, Integer> rs = new HashMap<>();

        SimpleMongoDbFactory factory       = new SimpleMongoDbFactory(new MongoClientURI("mongodb://localhost:27017/test"));
        MongoTemplate        mongoTemplate = new MongoTemplate(factory);

        MongoCollection<Document> bookColl         = mongoTemplate.getCollection("99lib-book");
        MongoCollection<Document> bookChaptorsColl = mongoTemplate.getCollection("99lib-book-chaptors");
        MongoCollection<Document> bookSectionsColl = mongoTemplate.getCollection("99lib-book-sections");

        long bookCount = bookColl.count();
        rs.put("book", (int) bookCount);
        System.out.println("book : " + bookCount);

        MongoCursor booksIt = bookColl.find().iterator();
        while (booksIt.hasNext()) {
            Document              book          = (Document) booksIt.next();
            String                bookId        = book.get("book_id").toString();
            MongoCursor<Document> chaptorDocsIt = bookChaptorsColl.find(new BasicDBObject("book_id", bookId)).iterator();
            while (chaptorDocsIt.hasNext()) {
                Document chaptorDoc = chaptorDocsIt.next();
                System.out.println(JSON.toJSONString(chaptorDoc));
                List<Document> chaptors = chaptorDoc.get("chaptors", new ArrayList<>());
                rs.put(String.format("book-%s", bookId), chaptors.size());
                System.out.println(String.format("book-%s : %s", bookId, chaptors.size()));
                chaptors.forEach(chaptor -> {
                    MongoCursor<Document> sectionsIt = bookSectionsColl
                        .find(new BasicDBObject("chaptor_id", chaptor.getString("chaptor_id"))).iterator();
                    if (sectionsIt.hasNext()) {
                        List<Document> sections = sectionsIt.next().get("content", new ArrayList<>());
                        rs.put(String.format("book-%s-%s", bookId, chaptor), sections.size());
                        System.out.println(String.format("book-%s-%s : %s", bookId, chaptor, sections.size()));
                    }
                });
            }
        }

        rs.keySet().forEach(key -> {
            System.out.println(key + " : " + rs.get(key));
        });

    }

}
