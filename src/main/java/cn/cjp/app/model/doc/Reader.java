package cn.cjp.app.model.doc;


import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = "reader")
@CompoundIndexes({@CompoundIndex(name = "uk_userId_bookDocId", def = "{'userId': 1, 'bookDocId': 1}", unique = true)})
@Data
@EqualsAndHashCode(callSuper = false)
public class Reader extends BaseDoc {

    private String userId;

    private String bookDocId;

    private int index;

}
