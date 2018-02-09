package cn.cjp.wechat.enumeration;

import java.util.ArrayList;
import java.util.List;

public enum Event {

    VIEW(100, "VIEW"),

    CLICK(200, "CLICK"),

    LOCATION(300, "LOCATION"),

    SCAN(400, "SCAN"),

    subscribe(500, "subscribe"),

    unsubscribe(600, "unsubscribe"),;

    private final int code;
    private final String message;

    public static Event fromValue(final int code) {
        for (Event t : Event.values()) {
            if (code == t.getValue()) {
                return t;
            }
        }

        return null;
    }

    public static Event fromDescription(String message) {
        for (Event t : Event.values()) {
            if (message == t.getDescription()) {
                return t;
            }
        }

        return null;
    }

    public static List<Event> fromValues(final Iterable<Integer> codes) {

        List<Event> ts = new ArrayList<>();

        for (Integer code : codes) {

            final Event t = fromValue(code);

            if (t != null) {
                ts.add(t);
            }
        }

        return ts;
    }

    Event(int code, String message) {
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
