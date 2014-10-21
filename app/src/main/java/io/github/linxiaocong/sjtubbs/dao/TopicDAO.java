package io.github.linxiaocong.sjtubbs.dao;

import io.github.linxiaocong.sjtubbs.models.Topic;
import io.github.linxiaocong.sjtubbs.utilities.BBSFetchr;

import java.util.ArrayList;

public class TopicDAO {
	
	public String getTopicList(String boardUrl, ArrayList<Topic> topicList) {
		return BBSFetchr.getTopicList(boardUrl, topicList);
	}
}
