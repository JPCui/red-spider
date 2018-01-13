package cn.cjp.spider.core.pipeline.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import cn.cjp.spider.core.config.SpiderConst;
import cn.cjp.utils.StringUtil;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * json -> mongodb
 * 
 * @author sucre
 *
 */
public class JsonPipeline implements Pipeline {

	private MongoTemplate mongoTemplate;

	/**
	 * by SpringDataMongoDB
	 * 
	 * @param mongoTemplate
	 */
	public JsonPipeline(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void process(ResultItems resultItems, Task task) {
		JSONObject json = resultItems.get("json");
		if (json == null) {
			this.insert(json);
		}

		List<JSONObject> jsons = resultItems.get("jsons");
		if (json == null) {
			this.insert(jsons);
		}

	}

	private void insert(JSONObject json) {
		DBCollection dbc = getCollection(json);
		dbc.insert(toDBObject(json));

		// TODO 唯一鍵應該在外面設置
		String uniqueKey = json.getString(SpiderConst.KEY_UNIQUE);
		if (StringUtil.isEmpty(uniqueKey)) {
			DBObject keyDbo = new BasicDBObject();
			keyDbo.put(uniqueKey, "1");
			dbc.createIndex(keyDbo, "uk_".concat(uniqueKey), true);
		}
	}

	private void insert(List<JSONObject> jsons) {
		jsons.forEach(json -> insert(json));
	}

	private DBCollection getCollection(JSONObject json) {
		return this.mongoTemplate.getCollection(json.getString(SpiderConst.KEY_TABLE_NAME));
	}

	private DBObject toDBObject(JSONObject json) {
		DBObject dbo = new BasicDBObject(json);
		return dbo;
	}

}
