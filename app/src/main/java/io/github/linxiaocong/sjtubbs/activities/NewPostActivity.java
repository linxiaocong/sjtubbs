package io.github.linxiaocong.sjtubbs.activities;

import android.os.Bundle;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.fragments.NewPostFragment;

/**
 * Created by linxiaocong on 2014/10/22.
 */
public class NewPostActivity extends SingleFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        getSupportActionBar().setTitle(R.string.action_newTopic);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, NewPostFragment.newInstance(args))
                .commit();
    }
}
