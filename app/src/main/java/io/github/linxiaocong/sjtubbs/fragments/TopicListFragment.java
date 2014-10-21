package io.github.linxiaocong.sjtubbs.fragments;

import java.util.ArrayList;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.activities.ReplyListActivity;
import io.github.linxiaocong.sjtubbs.dao.TopicDAO;
import io.github.linxiaocong.sjtubbs.models.Board;
import io.github.linxiaocong.sjtubbs.models.Topic;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TopicListFragment extends ListFragment {

	public static final String EXTRA_BOARD = "extra_board";
	
	private Board mBoard = null;
	private String mNextUrl = null;
	private boolean mIsLoading = false;
	private ArrayList<Topic> mTopicList = null;

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
		
		mBoard = (Board)getArguments().getSerializable(EXTRA_BOARD);
		getActivity().setTitle(mBoard.getName());

		(new FetchTopicListTask()).execute(mBoard.getUrl());
		setupAdapter();
		
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ListView listView = getListView();
		listView.setOnScrollListener(new OnScrollListener(){
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
	                int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0 && !mIsLoading) {
					mIsLoading = true;
					(new FetchTopicListTask()).execute(mNextUrl);
				}
			}
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}});
		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View footerView = inflater.inflate(R.layout.footer_topic_list, listView, false);
		listView.addFooterView(footerView);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (mTopicList == null)
			return;
		Topic topic = mTopicList.get(position);
		Intent i = new Intent(getActivity(), ReplyListActivity.class);
		i.putExtra(ReplyListFragment.EXTRA_TOPIC, topic);
		startActivity(i);
	}
	
	private void setupAdapter() {
		if (getActivity() == null)
			return;
		if (mTopicList != null) {
			TopicListAdapter adapter = new TopicListAdapter(mTopicList);
			setListAdapter(adapter);
		} else {
			setListAdapter(null);
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
				if (getListAdapter() == null) {
					setupAdapter();
				} else {
					TopicListAdapter adapter = (TopicListAdapter)getListAdapter();
					adapter.notifyDataSetChanged();
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
