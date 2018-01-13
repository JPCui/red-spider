package cn.cjp.spider.core.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 抓取的文本类型
 *
 * @author sucre
 */
public enum ParserType {

    BASE(0, "BASE"),

    JSON(10, "JSON"),

    DOM(20, "CSS"),

    REGEX(30, "REGEX"),

    XPATH(40, "XPATH"),

    /**
     * 从URL中提取<br>
     * url = http://abc.com?page=100<br>
     * pattern = http://abc.com?page=(.*)<br>
     * group = 1<br>
     * => 100<br>
     */
    URL_PATTERN(50, "URL_PATTERN"),
    
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
