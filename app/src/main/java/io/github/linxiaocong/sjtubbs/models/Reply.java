package io.github.linxiaocong.sjtubbs.models;

public class Reply {
	private String mUser;
	private String mTime;
	private String mTitle;
	private String mContent;
	private String mUrl;

	public Reply(String user, String time, String title, String content,
			String url) {
		mUser = user;
		mTime = time;
		mTitle = title;
		mContent = content;
		mUrl = url;
	}

	public String getUser() {
		return mUser;
	}

	public void setUser(String user) {
		mUser = user;
	}

	public String getTime() {
		return mTime;
	}

	public void setTime(String time) {
		mTime = time;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getContent() {
		return mContent;
	}

	public void setContent(String content) {
		mContent = content;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		mUrl = url;
	}

	@Override
	public String toString() {
		return "Reply [mUser=" + mUser + ", mContent=" + mContent + "]";
	}

}
