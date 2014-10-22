package io.github.linxiaocong.sjtubbs.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.fragments.TopicListFragment;
import io.github.linxiaocong.sjtubbs.models.Board;

public class TopicListActivity extends SingleFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Board board = (Board) getIntent().getSerializableExtra(TopicListFragment.EXTRA_BOARD);
        setTitle(board.getName());
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = TopicListFragment.newInstance(board);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }

}
