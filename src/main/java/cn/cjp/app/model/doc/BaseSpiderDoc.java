package cn.cjp.app.model.doc;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BaseSpiderDoc extends BaseDoc {
    private String _refer;
}
