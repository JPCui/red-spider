package cn.cjp.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import org.junit.Test;

import cn.cjp.Server;

public class MongoTest {

    @Test
    public void test() {

        MongoClient client = new MongoClient(Server.Mongo.host);
        MongoDatabase db = client.getDatabase("test");
        MongoIterable<String> it = db.listCollectionNames();
        MongoCursor<String> i = it.iterator();

        while (i.hasNext()) {
            System.out.println(i.next());
        }

    }


}
