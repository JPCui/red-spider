package cn.cjp.app.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public abstract class BaseRepository<T> {

	T t;

	@Autowired
	MongoTemplate mongoTemplate;

	public T findById(String _id) {
		return mongoTemplate.findById(_id, getTClass());
	}

	public T findOne(Query query) {
		return mongoTemplate.findOne(query, getTClass());
	}

	public List<T> find(Query query) {
		return mongoTemplate.find(query, getTClass());
	}

	public <D> List<D> find(Query query, Class<D> docClass) {
		return mongoTemplate.find(query, docClass);
	}

	abstract protected Class<T> getTClass();

}
