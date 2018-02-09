package cn.cjp.app.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cn.cjp.app.model.doc.Reader;

@Repository
public interface ReaderRepository extends MongoRepository<Reader, String> {
}
