package cn.cjp.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.junit.Test;

public class MongoTest {

    @Test
    public void test() {

        ConnectionString connString = new ConnectionString(
            "mongodb://localhost:27017/test"
        );
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connString)
            .retryWrites(true)
            .build();
        MongoClient client = MongoClients.create(settings);

        MongoDatabase         db = client.getDatabase("test");
        MongoIterable<String> it = db.listCollectionNames();
        MongoCursor<String>   i  = it.iterator();

        while (i.hasNext()) {
            System.out.println(i.next());
        }

        client.close();

    }

}
