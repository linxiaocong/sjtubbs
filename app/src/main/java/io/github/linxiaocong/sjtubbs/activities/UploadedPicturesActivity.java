package io.github.linxiaocong.sjtubbs.activities;

import android.os.Bundle;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.fragments.UploadedPicturesFragment;
import io.github.linxiaocong.sjtubbs.models.Board;

/**
 * Created by linxiaocong on 2014/10/23.
 */
public class UploadedPicturesActivity extends SingleFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Board board = (Board)getIntent().getSerializableExtra(UploadedPicturesFragment.EXTRA_BOARD);
        setTitle(board.getName() + " " + getResources().getString(R.string.action_uploadArea));
        UploadedPicturesFragment fragment = UploadedPicturesFragment.newInstance(board);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
