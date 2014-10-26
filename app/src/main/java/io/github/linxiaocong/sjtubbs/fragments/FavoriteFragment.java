package io.github.linxiaocong.sjtubbs.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class FavoriteFragment extends Fragment {

    private ArrayList<Board> mFavoriteBoards;
    private ListView mListView;
    private FavoriteBoardAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FavoriteBoardsDAO mFavoriteBoardsDAO;

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFavoriteBoards = new ArrayList<Board>();
        mFavoriteBoardsDAO = new FavoriteBoardsDAO(getActivity());
        mFavoriteBoardsDAO.getFavoriteBoards(mFavoriteBoards);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_list, container, false);
        mListView = (ListView)view.findViewById(R.id.list_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), TopicListActivity.class);
                intent.putExtra(TopicListFragment.EXTRA_BOARD, mFavoriteBoards.get(i));
                startActivity(intent);
            }
        });
        mAdapter = new FavoriteBoardAdapter();
        mListView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                mFavoriteBoards.clear();
                mFavoriteBoardsDAO.getFavoriteBoards(mFavoriteBoards);
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
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
