package cn.cjp.spider.core.pipeline.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import cn.cjp.spider.core.config.SpiderConst;
import cn.cjp.utils.Logger;
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

	private static final Logger LOGGER = Logger.getLogger(JsonPipeline.class);

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
		if (json != null) {
			this.insert(json);
		}

		List<JSONObject> jsons = resultItems.get("jsons");
		if (jsons != null) {
			this.insert(jsons);
		}

	}

	private void insert(JSONObject json) {
		DBCollection dbc = getCollection(json);

		// TODO 唯一鍵應該在外面設置
		// TODO 對於索引，可優化為結構化
		// 獲取唯一鍵
		String uniqueKey = json.getString(SpiderConst.KEY_UNIQUE);
		if (!StringUtil.isEmpty(uniqueKey)) {
			DBObject keyDbo = new BasicDBObject();
			keyDbo.put(uniqueKey, 1);
			dbc.createIndex(keyDbo, "uk_".concat(uniqueKey), true);
		}

		DBObject queryDBO = new BasicDBObject(uniqueKey, json.get(uniqueKey));
		DBObject updateDBO = toDBObject(json);
		WriteResult r = dbc.update(queryDBO, updateDBO, true, false);
		LOGGER.info(String.format("insert status %s", r.getN()));
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
