package io.github.linxiaocong.sjtubbs.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.activities.TopicListActivity;
import io.github.linxiaocong.sjtubbs.dao.FavoriteBoardsDAO;
import io.github.linxiaocong.sjtubbs.models.Board;

/**
 * Created by linxiaocong on 2014/10/26.
 */
public class FavoriteFragment extends ListFragment {

    private ArrayList<Board> mFavoriteBoards;

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFavoriteBoards = (new FavoriteBoardsDAO(getActivity())).getFavoriteBoards();
        setListAdapter(new FavoriteBoardAdapter());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getActivity(), TopicListActivity.class);
        intent.putExtra(TopicListFragment.EXTRA_BOARD, mFavoriteBoards.get(position));
        startActivity(intent);
    }

    class FavoriteBoardAdapter extends ArrayAdapter<Board> {

        public FavoriteBoardAdapter() {
            super(getActivity(), 0, mFavoriteBoards);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity()
                        .getLayoutInflater().inflate(R.layout.list_item_board, parent, false);
            }
            Board board = getItem(position);
            ((TextView)convertView).setText(board.getName() + " " + board.getDesc());
            return convertView;
        }
    }
}
