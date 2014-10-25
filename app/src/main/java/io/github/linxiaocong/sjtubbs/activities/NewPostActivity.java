package io.github.linxiaocong.sjtubbs.activities;

import android.os.Bundle;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.fragments.NewPostFragment;
import io.github.linxiaocong.sjtubbs.models.Board;

/**
 * Created by linxiaocong on 2014/10/22.
 */
public class NewPostActivity extends SingleFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Board board = (Board)getIntent().getSerializableExtra(NewPostFragment.EXTRA_BOARD);
        getSupportActionBar().setTitle(R.string.action_new_topic);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, NewPostFragment.newInstance(board))
                .commit();
    }
}
