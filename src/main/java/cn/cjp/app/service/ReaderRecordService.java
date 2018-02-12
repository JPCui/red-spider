package cn.cjp.app.service;

import com.mongodb.WriteResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import cn.cjp.app.model.doc.DocUtil;
import cn.cjp.app.model.doc.ReaderRecord;
import cn.cjp.app.model.doc.WxUserDoc;
import cn.cjp.app.repository.ReaderRecordRepository;
import cn.cjp.utils.Logger;

/**
 * reader service
 */
@Service
public class ReaderRecordService {

    private static final Logger LOGGER = Logger.getLogger(ReaderRecordService.class);

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    WxUserService wxUserService;

    @Autowired
    ReaderRecordRepository readerRecordRepository;

    public void save(String userId, String bookDocId, int index) throws IllegalAccessException {
        ReaderRecord record = new ReaderRecord();
        record.setBookDocId(bookDocId);
        record.setIndex(index);
        record.setUserId(userId);

        WriteResult writeResult = mongoTemplate.upsert(
            Query.query(Criteria.where("userId").is(userId).and("bookDocId").is(bookDocId)),
            Update.fromDBObject(DocUtil.bean2DBObject(record), "_id"), ReaderRecord.class);
        LOGGER.info(writeResult.toString());
    }

    /**
     *
     * @param userId
     * @return
     */
    public ReaderRecord findLatestByUserId(String userId) {
        ReaderRecord readerRecord = new ReaderRecord();
        readerRecord.setUserId(userId);

        Sort sort = new Sort(Sort.Direction.DESC, "_updateDate");
        Query query = Query.query(Criteria.where("userId").is(userId)).with(sort);
        ReaderRecord record = mongoTemplate.findOne(query, ReaderRecord.class);
        return record;
    }

    public ReaderRecord findLatestByOpenid(String openid) {
        WxUserDoc wxUserDoc = wxUserService.findOneByOpenid(openid);
        ReaderRecord readerRecord = this.findLatestByUserId(wxUserDoc.get_id());
        return readerRecord;
    }

}
