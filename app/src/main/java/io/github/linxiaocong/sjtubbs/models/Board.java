package io.github.linxiaocong.sjtubbs.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by linxiaocong on 2014/9/24.
 */
public class Board implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String JSON_NAME = "name";
	public static final String JSON_DESC = "desc";
	public static final String JSON_URL = "url";
	public static final String JSON_HAS_SUB_BOARDS = "has_sub_boards";
	public static final String JSON_SUB_BOARDS = "sub_boards";

	private String mName;
	private String mDesc;
	private String mUrl;
	private boolean mHasSubBoards;
	private ArrayList<Board> mSubBoards;

	public Board(String name, String desc, String url) {
		mName = name;
		mDesc = desc;
		mUrl = url;
	}

	public Board(String name, String desc, String url,
			ArrayList<Board> subBoards) {
		mName = name;
		mDesc = desc;
		mUrl = url;
		mHasSubBoards = true;
		mSubBoards = subBoards;
	}

	public Board(JSONObject json) throws JSONException {
		mName = json.getString(JSON_NAME);
		mDesc = json.getString(JSON_DESC);
		mUrl = json.getString(JSON_URL);
		mHasSubBoards = json.getBoolean(JSON_HAS_SUB_BOARDS);
		if (mHasSubBoards) {
			mSubBoards = new ArrayList<Board>();
			JSONArray arr = json.getJSONArray(JSON_SUB_BOARDS);
			for (int i = 0; i < arr.length(); ++i) {
				Board subBoard = new Board(arr.getJSONObject(i));
				mSubBoards.add(subBoard);
			}
		}
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getDesc() {
		return mDesc;
	}

	public void setDesc(String desc) {
		mDesc = desc;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		mUrl = url;
	}

	public boolean hasSubBoard() {
		return mHasSubBoards;
	}

	public void setHasSubBoard(boolean hasSubBoards) {
		mHasSubBoards = hasSubBoards;
	}

	public ArrayList<Board> getSubBoards() {
		return mSubBoards;
	}

	@Override
	public String toString() {
		return "Board{" + "mName='" + mName + '\'' + ", mDesc='" + mDesc + '\''
				+ ", mUrl='" + mUrl + '\'' + ", mHasSubBoards=" + mHasSubBoards
				+ ", mSubBoards=" + mSubBoards + '}';
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_NAME, mName);
		json.put(JSON_DESC, mDesc);
		json.put(JSON_URL, mUrl);
		json.put(JSON_HAS_SUB_BOARDS, mHasSubBoards);
		if (mHasSubBoards) {
			JSONArray arr = new JSONArray();
			for (Board board : mSubBoards) {
				arr.put(board.toJSON());
			}
			json.put(JSON_SUB_BOARDS, arr);
		} else {
			json.put(JSON_SUB_BOARDS, null);
		}
		return json;
	}
}
