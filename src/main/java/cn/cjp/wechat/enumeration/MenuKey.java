package cn.cjp.wechat.enumeration;

import java.util.ArrayList;
import java.util.List;

public enum MenuKey {

    BOOK_READ_PREV(101, "BOOK_READ_PREV"),

    BOOK_READ_NEXT(102, "BOOK_READ_NEXT"),

    BOOK_READ(103, "BOOK_READ"),

    ;

    private final int code;
    private final String message;

    public static MenuKey fromValue(final int code) {
        for (MenuKey t : MenuKey.values()) {
            if (code == t.getValue()) {
                return t;
            }
        }

        return null;
    }

    public static MenuKey fromDescription(String message) {
        for (MenuKey t : MenuKey.values()) {
            if (message == t.getDescription()) {
                return t;
            }
        }

        return null;
    }

    public static List<MenuKey> fromValues(final Iterable<Integer> codes) {

        List<MenuKey> ts = new ArrayList<>();

        for (Integer code : codes) {

            final MenuKey t = fromValue(code);

            if (t != null) {
                ts.add(t);
            }
        }

        return ts;
    }

    MenuKey(int code, String message) {
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
