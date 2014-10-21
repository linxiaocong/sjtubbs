package io.github.linxiaocong.sjtubbs.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by linxiaocong on 2014/9/24.
 */
public class Section implements Serializable {

	private static final long serialVersionUID = 1L;

	private String mName;
	private String mUrl;
	private ArrayList<Board> mBoards = null;

	public static final String JSON_NAME = "name";
	public static final String JSON_URL = "url";
	public static final String JSON_BOARDS = "boards";

	public Section(String name, String url, ArrayList<Board> boards) {
		mName = name;
		mUrl = url;
		mBoards = boards;
	}

	public Section(JSONObject json) throws JSONException {
		mName = json.getString(JSON_NAME);
		mUrl = json.getString(JSON_URL);
		mBoards = new ArrayList<Board>();
		JSONArray arr = json.getJSONArray(JSON_BOARDS);
		for (int i = 0; i < arr.length(); ++i) {
			mBoards.add(new Board(arr.getJSONObject(i)));
		}
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		mUrl = url;
	}

	public ArrayList<Board> getBoards() {
		return mBoards;
	}

	@Override
	public String toString() {
		return "Section{" + "mUrl='" + mUrl + '\'' + ", mName='" + mName + '\''
				+ '}';
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_NAME, mName);
		json.put(JSON_URL, mUrl);
		JSONArray arr = new JSONArray();
		for (Board board : mBoards) {
			arr.put(board.toJSON());
		}
		json.put(JSON_BOARDS, arr);
		return json;
	}
}
