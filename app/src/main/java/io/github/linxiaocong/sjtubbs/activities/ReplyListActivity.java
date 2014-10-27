package io.github.linxiaocong.sjtubbs.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.fragments.ReplyListFragment;
import io.github.linxiaocong.sjtubbs.models.Topic;

public class ReplyListActivity extends SingleFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Topic topic = (Topic) getIntent().getSerializableExtra(
                ReplyListFragment.EXTRA_TOPIC);
        setTitle(topic.getTitle());
        Fragment fragment = ReplyListFragment.newInstance(topic);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }
}
