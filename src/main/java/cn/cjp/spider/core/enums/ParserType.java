package cn.cjp.spider.core.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 抓取的文本类型
 * 
 * @author sucre
 *
 */
public enum ParserType {

    JSON(10, "JSON"),

    HTML(20, "HTML"),

    REGEX(30, "REGEX"),

    ;

    private final int code;
    private final String message;

    public static ParserType fromValue(final int code) {
        for (ParserType salaryUnit : ParserType.values()) {
            if (code == salaryUnit.getValue()) {
                return salaryUnit;
            }
        }

        return null;
    }

    public static List<ParserType> fromValues(final Iterable<Integer> codes) {

        List<ParserType> jobTags = new ArrayList<>();

        for (Integer code : codes) {

            final ParserType jobTag = fromValue(code);

            if (jobTag != null) {
                jobTags.add(jobTag);
            }
        }

        return jobTags;
    }

    ParserType(int code, String message) {
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
