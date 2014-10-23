package io.github.linxiaocong.sjtubbs.fragments;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.activities.NewTopicActivity;
import io.github.linxiaocong.sjtubbs.activities.ReplyListActivity;
import io.github.linxiaocong.sjtubbs.dao.TopicDAO;
import io.github.linxiaocong.sjtubbs.models.Board;
import io.github.linxiaocong.sjtubbs.models.Topic;

public class TopicListFragment extends Fragment {

    public static final String EXTRA_BOARD = "extra_board";

    private Board mBoard;
    private String mNextUrl;
    private boolean mIsLoading = false;
    private ArrayList<Topic> mTopicList;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static TopicListFragment newInstance(Board board) {
        TopicListFragment fragment = new TopicListFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_BOARD, board);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBoard = (Board) getArguments().getSerializable(EXTRA_BOARD);
        getActivity().setTitle(mBoard.getName());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TopicListAdapter adapter = (TopicListAdapter) mListView.getAdapter();
                try {
                    mTopicList.clear();
                    adapter.notifyDataSetChanged();
                    mNextUrl = null;
                    (new FetchTopicListTask()).execute(mBoard.getUrl());
                    mSwipeRefreshLayout.setRefreshing(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mListView = (ListView) view.findViewById(R.id.list_view);

        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount &&
                        totalItemCount > 1 && !mIsLoading && mNextUrl != null) {
                    mIsLoading = true;
                    (new FetchTopicListTask()).execute(mNextUrl);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mTopicList == null)
                    return;
                Topic topic = mTopicList.get(i);
                Intent intent = new Intent(getActivity(), ReplyListActivity.class);
                intent.putExtra(ReplyListFragment.EXTRA_TOPIC, topic);
                startActivity(intent);
            }
        });

        (new FetchTopicListTask()).execute(mBoard.getUrl());
        setupAdapter();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_topic_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_topic:
                Intent intent = new Intent(getActivity(), NewTopicActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupAdapter() {
        if (getActivity() == null)
            return;
        if (mTopicList != null) {
            TopicListAdapter adapter = new TopicListAdapter(mTopicList);
            mListView.setAdapter(adapter);
        } else {
            mListView.setAdapter(null);
        }
    }

    private class FetchTopicListTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String boardUrl = params[0];
            try {
                if (mTopicList == null) {
                    mTopicList = new ArrayList<Topic>();
                }
                TopicDAO topicDAO = new TopicDAO();
                return topicDAO.getTopicList(boardUrl, mTopicList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mIsLoading = false;
            if (result != null) {
                mNextUrl = result;
                if (mListView.getAdapter() == null) {
                    setupAdapter();
                } else {
                    TopicListAdapter adapter = (TopicListAdapter) mListView.getAdapter();
                    adapter.notifyDataSetChanged();
                }
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }
    }

    private class TopicListAdapter extends ArrayAdapter<Topic> {

        public TopicListAdapter(ArrayList<Topic> topics) {
            super(getActivity(), 0, topics);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.list_item_topic, parent, false);
            }
            Topic topic = getItem(position);
            TextView textviewTitle = (TextView) convertView
                    .findViewById(R.id.textview_title);
            textviewTitle.setText(topic.getTitle());
            return convertView;
        }
    }
}
