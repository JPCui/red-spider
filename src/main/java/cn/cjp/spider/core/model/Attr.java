package cn.cjp.spider.core.model;

import cn.cjp.spider.core.enums.ParserType;
import lombok.Data;

/**
 * 字段爬取规则Model
 * 
 * @author sucre
 *
 */
@Data
public class Attr {

    public Attr() {
    }

    public Attr(String parserPath, String field, ParserType parserType) {
        this.parserPath = parserPath;
        this.field = field;
        this.parserType = parserType.getValue();
    }

    /**
     * 解析路径
     */
    String parserPath;

    String parserPathAttr;

    /**
     * 字段名
     */
    String field;

    /**
     * 解析类型
     * 
     * @see ParserType
     */
    Integer parserType;

    boolean isMulti;

    private Attr nested;

}
