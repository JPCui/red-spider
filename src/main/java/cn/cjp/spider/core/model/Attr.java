package cn.cjp.spider.core.model;

import cn.cjp.spider.core.enums.ParserType;

/**
 * 
 * @author sucre
 *
 */
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

    private Attr nested;

    public String getParserPath() {
        return parserPath;
    }

    public void setParserPath(String parserPath) {
        this.parserPath = parserPath;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Integer getParserType() {
        return parserType;
    }

    public void setParserType(Integer parserType) {
        this.parserType = parserType;
    }

    public String getParserPathAttr() {
        return parserPathAttr;
    }

    public void setParserPathAttr(String parserPathAttr) {
        this.parserPathAttr = parserPathAttr;
    }

    public Attr getNested() {
        return nested;
    }

    public void setNested(Attr nested) {
        this.nested = nested;
    }

}
