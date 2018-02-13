package cn.cjp.wechat.enumeration;

import java.util.ArrayList;
import java.util.List;

public enum MsgType {

	TEXT(100, "text"),

	IMAGE(200, "image"),

	VOICE(300, "voice"),

	VIDEO(400, "video"),

	SHORTVIDEO(500, "shortvideo"),

	LOCATION(600, "location"),

	EVENT(700, "event"),

	NEWS(800, "news"),

	MUSIC(900, "music"),

	;

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
			if (t.getDescription().equalsIgnoreCase(message)) {
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
