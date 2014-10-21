package io.github.linxiaocong.sjtubbs.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Topic implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String JSON_ID = "id";
	private static final String JSON_BOARD = "board";
	private static final String JSON_TITLE = "title";
	private static final String JSON_AUTHOR = "author";
	private static final String JSON_URL = "url";
	private String mId;
	private String mBoard;
	private String mTitle;
	private String mAuthor;
	private String mUrl;

	public Topic(String id, String board, String title, String author,
			String url) {
		mId = id;
		mBoard = board;
		mTitle = title;
		mAuthor = author;
		mUrl = url;
	}

	public Topic(JSONObject json) throws JSONException {
		mId = json.getString(JSON_ID);
		mBoard = json.getString(JSON_BOARD);
		mTitle = json.getString(JSON_TITLE);
		mAuthor = json.getString(JSON_AUTHOR);
		mUrl = json.getString(JSON_URL);
	}

	public String getId() {
		return mId;
	}

	public String getBoard() {
		return mBoard;
	}

	public void setBoard(String board) {
		mBoard = board;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getAuthor() {
		return mAuthor;
	}

	public void setAuthor(String author) {
		mAuthor = author;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		mUrl = url;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId);
		json.put(JSON_BOARD, mBoard);
		json.put(JSON_TITLE, mTitle);
		json.put(JSON_AUTHOR, mAuthor);
		json.put(JSON_URL, mUrl);
		return json;
	}

	@Override
	public String toString() {
		return "[" + mBoard + "] " + mTitle;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Topic) {
			Topic topic = (Topic)object;
			return mId.equals(topic.mId);
		}
		return super.equals(object);
	}

	@Override
	public int hashCode() {
		return mId.hashCode();
	}
	
	
}
