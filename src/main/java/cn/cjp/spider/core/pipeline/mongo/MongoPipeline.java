package cn.cjp.spider.core.pipeline.mongo;

import cn.cjp.spider.core.config.SpiderConst;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * json -> mongodb
 *
 * @author sucre
 */
@Slf4j
public class MongoPipeline implements Pipeline {

    private MongoTemplate mongoTemplate;

    /**
     * by SpringDataMongoDB
     */
    public MongoPipeline(MongoTemplate mongoTemplate) {
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
        MongoCollection dbc = getCollection(json);

        // TODO 唯一鍵應該在外面設置
        // TODO 對於索引，可優化為結構化
        // 獲取唯一鍵
        String uniqueKey = json.getString(SpiderConst.KEY_UNIQUE);
        if (StringUtils.hasText(uniqueKey)) {
            Document keyDbo = new Document();
            keyDbo.put(uniqueKey, 1);
            dbc.createIndex(keyDbo, new IndexOptions().name("uk_".concat(uniqueKey)).unique(true));
        }

        Document     queryDBO     = new Document(uniqueKey, json.get(uniqueKey));
        Document     updateDBO    = toDBObject(json);
        UpdateResult updateResult = dbc.replaceOne(queryDBO, updateDBO, new ReplaceOptions().upsert(true));
        log.info(String.format("insert status %s", updateResult.getModifiedCount()));
    }

    private void insert(List<JSONObject> jsons) {
        jsons.forEach(json -> insert(json));
    }

    private MongoCollection<Document> getCollection(JSONObject json) {
        return this.mongoTemplate.getCollection(json.getString(SpiderConst.KEY_TABLE_NAME));
    }

    private Document toDBObject(JSONObject json) {
        Document dbo = new Document(json);
        dbo.put("_updateDate", new Date());
        return dbo;
    }

}
