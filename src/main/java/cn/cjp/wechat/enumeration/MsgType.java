package cn.cjp.wechat.enumeration;

import java.util.ArrayList;
import java.util.List;


public enum MsgType {

    text(100, "text"),

    image(200, "image"),

    voice(300, "voice"),

    video(400, "video"),

    shortvideo(500, "shortvideo"),

    location(600, "location"),

    event(700, "event"),;

    private final int code;
    private final String message;

    public static MsgType fromValue(final int code) {
        for (MsgType t : MsgType.values()) {
            if (code == t.getValue()) {
                return t;
            }
        }

        return null;
    }

    public static MsgType fromDescription(String message) {
        for (MsgType t : MsgType.values()) {
            if (message == t.getDescription()) {
                return t;
            }
        }

        return null;
    }

    public static List<MsgType> fromValues(final Iterable<Integer> codes) {

        List<MsgType> ts = new ArrayList<>();

        for (Integer code : codes) {

            final MsgType t = fromValue(code);

            if (t != null) {
                ts.add(t);
            }
        }

        return ts;
    }

    MsgType(int code, String message) {
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
