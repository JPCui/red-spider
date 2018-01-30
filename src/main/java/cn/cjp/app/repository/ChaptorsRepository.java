package cn.cjp.app.repository;

import org.springframework.stereotype.Repository;

import cn.cjp.app.model.doc.Chaptors;

@Repository
public class ChaptorsRepository extends BaseRepository<Chaptors> {

	@Override
	protected Class<Chaptors> getTClass() {
		return Chaptors.class;
	}

}
