package cn.cjp.spider.core.mongo;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ResourceUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.GroupCommand;
import com.mongodb.MongoClientURI;

import cn.cjp.app.model.doc.Sections;
import redis.clients.jedis.Jedis;

public class CheckCrawlNum {

	MongoTemplate mongoTemplate;

	public CheckCrawlNum() throws UnknownHostException {
		SimpleMongoDbFactory factory = new SimpleMongoDbFactory(new MongoClientURI("mongodb://localhost:27017/test"));
		mongoTemplate = new MongoTemplate(factory);
	}

	@Test
	public void getNum() throws IOException {

		File countReduceFile = ResourceUtils.getFile("classpath:mongo/script/count.reduce");
		String countReduce = FileUtils.readFileToString(countReduceFile);

		File aKeyfFile = ResourceUtils.getFile("classpath:mongo/script/a.keyf");
		String aKeyf = FileUtils.readFileToString(aKeyfFile);

		DBCollection coll = mongoTemplate.getCollection("99lib-book");

		GroupCommand command = new GroupCommand(coll.find().getCollection(), new BasicDBObject("_id", 1),
				new BasicDBObject("keyf", aKeyf), new BasicDBObject("count", 0), countReduce, null);
		DBObject g = coll.group(command);
		System.out.println(g);

		// Filters.

	}

	@Test
	public void testBefore0121() {
		int page = 0;
		int pageSize = 1000;

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 15);

		Jedis jedis = new Jedis();

		List<Sections> list = null;
		while (true) {
			Criteria criteria = Criteria.where("_updateDate").lt(cal.getTime());
			Query query = Query.query(criteria).skip(((page++) - 1) * pageSize).limit(pageSize);
			list = mongoTemplate.find(query, Sections.class);
			if (list.size() == 0) {
				break;
			}

			List<String> refers = list.stream().map(sec -> {
				return sec.get_refer();
			}).collect(Collectors.toList());

			System.out.println("push: " + refers);
			Long l = jedis.lpush("queue_99lib.net", refers.toArray(new String[0]));
			if (l < 1) {
				System.err.println("push err: " + refers);
			}
		}

		jedis.close();
	}

}
