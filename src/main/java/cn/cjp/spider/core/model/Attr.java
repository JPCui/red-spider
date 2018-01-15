package cn.cjp.spider.core.model;

import cn.cjp.spider.core.enums.ParserType;
import lombok.Data;

/**
 * 字段爬取规则Model
 *
 * @author sucre
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
	 * 解析类型（必需）
	 *
	 * @see ParserType
	 */
	Integer parserType;

	/**
	 * 是否有多个值
	 */
	boolean isMulti;

	/**
	 * 是否具有唯一性
	 */
	private boolean isUnique = false;

	private Attr nested;

	/**
	 * 是否过滤重复的值（当isMulti=1）
	 */
	private int filterRepeat = 0;

	/**
	 * 默认值
	 */
	private String defaultValue;

}
