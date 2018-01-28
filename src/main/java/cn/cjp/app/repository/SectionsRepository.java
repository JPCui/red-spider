package cn.cjp.app.repository;

import org.springframework.stereotype.Repository;

import cn.cjp.app.model.doc.Sections;

@Repository
public class SectionsRepository extends BaseRepository<Sections> {

	@Override
	protected Class<Sections> getTClass() {
		return Sections.class;
	}

}
