package cn.cjp.spider.core;

import org.bson.Document;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoClientTest {

	@Test
	public void test() throws Exception {
		MongoClient client = new MongoClient();
		MongoDatabase db = client.getDatabase("test");
		MongoCollection<Document> coll = db.getCollection("test_coll");

		Document doc = new Document("uu", "");
		System.out.println(doc);
		coll.insertOne(doc);
		System.out.println(doc);

		client.close();
	}

}
