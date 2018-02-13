package cn.cjp.wechat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.cjp.robot.tuling.api.TulingApi;
import weixin.popular.bean.xmlmessage.XMLMessage;
import weixin.popular.bean.xmlmessage.XMLNewsMessage;
import weixin.popular.bean.xmlmessage.XMLNewsMessage.Article;
import weixin.popular.bean.xmlmessage.XMLTextMessage;

public class MsgUtil {

	private static final Logger LOCAL = Logger.getLogger("LOCAL");

	public static final String KEY_FromUserName = "FromUserName";
	public static final String KEY_ToUserName = "ToUserName";
	public static final String KEY_CreateTime = "CreateTime";
	public static final String KEY_MsgType = "MsgType";
	public static final String KEY_Event = "Event";
	public static final String KEY_Content = "Content";
	public static final String KEY_MsgId = "MsgId";
	public static final String KEY_ArticleCount = "ArticleCount";
	public static final String KEY_Articles = "Articles";
	public static final String KEY_item = "item";
	public static final String KEY_Title = "Title";
	public static final String KEY_Description = "Description";
	public static final String KEY_PicUrl = "PicUrl";
	public static final String KEY_Url = "Url";

	public static XMLMessage handlerTulingText(String fromUserName, String toUserName, String text) {
		JSONObject json = new JSONObject(text);
		int code = json.getInt("code");

		if (code == TulingApi.CODE_TEXT) {
			return toText(fromUserName, toUserName, json.getString("text"));
		} else if (code == TulingApi.CODE_LINK) {
			return toText(fromUserName, toUserName, json.getString("text") + "\r\n" + json.getString("url"));
		} else if (code == TulingApi.CODE_NEWS) {
			JSONArray newsList = json.getJSONArray("list");
			List<Article> msgList = new ArrayList<>();
			for (int i = 0; i < newsList.length() && i < 10; i++) {
				JSONObject newsJson = newsList.getJSONObject(i);
				String arti = newsJson.getString("article");
				String source = newsJson.getString("source");
				String icon = newsJson.getString("icon");
				String detailurl = newsJson.optString("detailurl");

				Article article = new Article();
				article.setTitle(arti);
				article.setDescription(source);
				article.setPicurl(icon);
				article.setUrl(detailurl);
				msgList.add(article);
			}
			return toNews(fromUserName, toUserName, msgList);
		} else if (code == TulingApi.CODE_MENU) {
			JSONArray list = json.getJSONArray("list");
			List<Article> newsList = new ArrayList<>();
			for (int i = 0; i < list.length() && i < 10; i++) {
				JSONObject newsJson = list.getJSONObject(i);
				String name = newsJson.getString("name");
				String info = newsJson.getString("info");
				String icon = newsJson.getString("icon");
				String detailurl = newsJson.getString("detailurl");

				Article article = new Article();
				article.setTitle(name);
				article.setDescription(info);
				article.setPicurl(icon);
				article.setUrl(detailurl);
				newsList.add(article);
			}
			return toNews(fromUserName, toUserName, newsList);
		} else if (code == TulingApi.CODE_CHILDREN_ERGE) {
			JSONObject func = json.getJSONObject("function");
			return toText(fromUserName, toUserName, "给你推荐：" + func.optString("singer") + "-" + func.getString("song"));
		} else if (code == TulingApi.CODE_CHILDREN_POETRY) {
			JSONObject func = json.getJSONObject("function");
			return toText(fromUserName, toUserName, "来一首：" + func.optString("name") + "-" + func.getString("author"));
		} else {
			Exception e = new Exception("unknown tuling code.");
			LOCAL.error(json, e);
			return toText(fromUserName, toUserName, "?");
		}
	}

	public static XMLNewsMessage toNews(String fromUserName, String toUserName, List<Article> articles) {
		XMLNewsMessage message = new XMLNewsMessage(fromUserName, toUserName, articles);
		return message;
	}

	/**
	 * 回复消息，转换成微信text类型的XML
	 * 
	 * @param fromUserName
	 * @param toUserName
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public static XMLTextMessage toText(String fromUserName, String toUserName, String content) {

		XMLTextMessage message = new XMLTextMessage(fromUserName, toUserName, content);
		return message;
	}

}
