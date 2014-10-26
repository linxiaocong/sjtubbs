package io.github.linxiaocong.sjtubbs.dao;

import java.util.ArrayList;

import io.github.linxiaocong.sjtubbs.models.Topic;
import io.github.linxiaocong.sjtubbs.utilities.BBSUtils;

public class TopicDAO {

    public String getTopicList(String boardUrl, String boardName, ArrayList<Topic> topicList) {
        return BBSUtils.getInstance().getTopicList(boardUrl, boardName, topicList);
    }
}
