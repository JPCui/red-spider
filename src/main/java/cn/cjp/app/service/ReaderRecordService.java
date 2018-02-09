package cn.cjp.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import cn.cjp.app.model.doc.ReaderRecord;
import cn.cjp.app.repository.ReaderRecordRepository;

/**
 * reader service
 */
@Service
public class ReaderRecordService {

    @Autowired
    ReaderRecordRepository readerRecordRepository;

    public void save(String userId, String bookDocId, int index) {
        ReaderRecord reader = new ReaderRecord();
        reader.setBookDocId(bookDocId);
        reader.setIndex(index);
        reader.setUserId(userId);
        readerRecordRepository.save(reader);
    }

    public ReaderRecord findOne(String userId) {
        ReaderRecord readerRecord = new ReaderRecord();
        readerRecord.setUserId(userId);

        Example<ReaderRecord> example = Example.of(readerRecord);

        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "_updateDate");
        Sort sort = new Sort(order);
        PageRequest pageRequest = new PageRequest(1, 1, sort);
        Page<ReaderRecord> records = readerRecordRepository.findAll(example, pageRequest);
        if (records.hasContent()) {
            return records.getContent().get(0);
        }
        return null;
    }


}
