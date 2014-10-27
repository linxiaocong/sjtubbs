package io.github.linxiaocong.sjtubbs.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.fragments.ReplyListFragment;
import io.github.linxiaocong.sjtubbs.models.Topic;

public class ReplyListActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_without_margin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Topic topic = (Topic) getIntent().getSerializableExtra(
                ReplyListFragment.EXTRA_TOPIC);
        setTitle(topic.getTitle());
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = ReplyListFragment.newInstance(topic);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }
}
