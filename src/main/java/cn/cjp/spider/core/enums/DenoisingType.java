package cn.cjp.spider.core.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 去噪類型
 * 
 * @author sucre
 *
 */
public enum DenoisingType {

	_99LIB_SECTIONS(10, "_99LIB_SECTIONS"),

	/**
	 * 利用正则表达式 移除无关内容
	 * 
	 * 内容里出现类似 99lib 的文字
	 */
	_99LIB_TEXT_REGEX_REMOVE(20, "_99LIB_TEXT_REGEX_REMOVE"),

	;

	private final int code;
	private final String message;

	public static DenoisingType fromValue(final int code) {
		for (DenoisingType salaryUnit : DenoisingType.values()) {
			if (code == salaryUnit.getValue()) {
				return salaryUnit;
			}
		}

		return null;
	}

	public static List<DenoisingType> fromValues(final Iterable<Integer> codes) {

		List<DenoisingType> jobTags = new ArrayList<>();

		for (Integer code : codes) {

			final DenoisingType jobTag = fromValue(code);

			if (jobTag != null) {
				jobTags.add(jobTag);
			}
		}

		return jobTags;
	}

	DenoisingType(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getValue() {
		return code;
	}

	public String getDescription() {
		return message;
	}

}
