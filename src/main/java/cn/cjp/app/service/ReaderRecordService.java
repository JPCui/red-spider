package cn.cjp.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;

import cn.cjp.app.model.doc.ReaderRecord;
import cn.cjp.utils.JacksonUtil;

/**
 * reader service
 */
@Service
public class ReaderRecordService {

	@Autowired
	MongoTemplate mongoTemplate;

	public void save(String userId, String bookDocId, int index) {
		ReaderRecord record = new ReaderRecord();
		record.setBookDocId(bookDocId);
		record.setIndex(index);
		record.setUserId(userId);

		WriteResult writeResult = mongoTemplate.upsert(
				Query.query(Criteria.where("userId").is(userId).and("bookDocId").is(bookDocId)),
				Update.fromDBObject(BasicDBObject.parse(JacksonUtil.toJson(record))), ReaderRecord.class);
		System.out.println(writeResult);
	}

	public ReaderRecord findOne(String userId) {
		ReaderRecord readerRecord = new ReaderRecord();
		readerRecord.setUserId(userId);

		Sort sort = new Sort(Sort.Direction.DESC, "_updateDate");
		Query query = Query.query(Criteria.where("userId").is(userId)).with(sort);
		ReaderRecord record = mongoTemplate.findOne(query, ReaderRecord.class);
		return record;
	}

}
