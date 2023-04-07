package cn.cjp.spider.core.mongo;

import com.alibaba.fastjson.JSON;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import java.util.HashMap;
import java.util.Map;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.Document;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

public class DocumentCheckTest {

    MongoTemplate mongoTemplate;

    @Test
    public void checkBookNums() {
        Map<String, Integer> rs = new HashMap<>();

        MongoCollection<Document> bookColl         = mongoTemplate.getCollection("99lib-book");
        MongoCollection<Document> bookChaptorsColl = mongoTemplate.getCollection("99lib-book-chaptors");
        MongoCollection<Document> bookSectionsColl = mongoTemplate.getCollection("99lib-book-sections");

        long bookCount = bookColl.countDocuments();
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
                BsonArray chaptors = chaptorDoc.get("chaptors", BsonArray.class);
                rs.put(String.format("book-%s", bookId), chaptors.size());
                System.out.println(String.format("book-%s : %s", bookId, chaptors.size()));
                chaptors.forEach(t -> {
                    BsonDocument chaptor = t.asDocument();
                    MongoCursor<Document> sectionsIt = bookSectionsColl
                        .find(new BasicDBObject("chaptor_id", chaptor.getString("chaptor_id"))).iterator();
                    if (sectionsIt.hasNext()) {
                        BsonArray sections = sectionsIt.next().get("content", BsonArray.class);
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
