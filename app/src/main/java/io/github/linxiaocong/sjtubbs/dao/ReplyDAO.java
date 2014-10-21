package io.github.linxiaocong.sjtubbs.dao;

import android.content.Context;

import java.util.ArrayList;

import io.github.linxiaocong.sjtubbs.models.Reply;
import io.github.linxiaocong.sjtubbs.utilities.BBSFetchr;

public class ReplyDAO {
	private Context mContext;

	public ReplyDAO(Context c) {
		mContext = c;
	}

	public Context getContext() {
		return mContext;
	}

	public String getReplyList(String topicUrl, ArrayList<Reply> replyList) {
		return BBSFetchr.getReplyList(topicUrl, replyList);
	}
}
