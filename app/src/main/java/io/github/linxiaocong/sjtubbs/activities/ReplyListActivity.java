package io.github.linxiaocong.sjtubbs.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
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
		FragmentManager fragmentManager = getFragmentManager();
		Fragment fragment = ReplyListFragment.newInstance(topic);
		fragmentManager.beginTransaction()
				.replace(R.id.fragment_container, fragment).commit();
	}
}
