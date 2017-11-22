package cn.cjp.spider.base.enums;

import java.util.ArrayList;
import java.util.List;

public enum ContentType {

    JSON(10, "JSON"),

    HTML(20, "HTML"),

    ;

    private final int code;
    private final String message;

    public static ContentType fromValue(final int code) {
        for (ContentType salaryUnit : ContentType.values()) {
            if (code == salaryUnit.getValue()) {
                return salaryUnit;
            }
        }

        return null;
    }

    public static List<ContentType> fromValues(final Iterable<Integer> codes) {

        List<ContentType> jobTags = new ArrayList<>();

        for (Integer code : codes) {

            final ContentType jobTag = fromValue(code);

            if (jobTag != null) {
                jobTags.add(jobTag);
            }
        }

        return jobTags;
    }

    ContentType(int code, String message) {
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
