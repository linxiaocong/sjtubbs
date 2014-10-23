package io.github.linxiaocong.sjtubbs.activities;

import android.os.Bundle;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.fragments.NewTopicFragment;

/**
 * Created by linxiaocong on 2014/10/22.
 */
public class NewTopicActivity extends SingleFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.action_new_topic);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, NewTopicFragment.newInstance())
                .commit();
    }
}
