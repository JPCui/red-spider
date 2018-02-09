package cn.cjp.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.cjp.app.model.doc.Reader;
import cn.cjp.app.repository.ReaderRepository;

/**
 * reader service
 */
@Service
public class ReaderService {

    @Autowired
    ReaderRepository readerRepository;

    public void save(String userId, String bookDocId, int index) {
        Reader reader = new Reader();
        reader.setBookDocId(bookDocId);
        reader.setIndex(index);
        reader.setUserId(userId);
        readerRepository.save(reader);
    }

}
