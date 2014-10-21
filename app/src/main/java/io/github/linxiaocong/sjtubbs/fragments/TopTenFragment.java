package io.github.linxiaocong.sjtubbs.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.activities.ReplyListActivity;
import io.github.linxiaocong.sjtubbs.dao.TopTenDAO;
import io.github.linxiaocong.sjtubbs.models.Topic;

public class TopTenFragment extends ListFragment {

	private ArrayList<Topic> mTopTen;

	public static TopTenFragment newInstance() {
		TopTenFragment fragment = new TopTenFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AsyncTask<Void, Void, Void> fetchTopTenTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				TopTenDAO topTenDAO = new TopTenDAO(getActivity());
				try {
					mTopTen = topTenDAO.getTopTen();
				} catch (Exception e) {
					e.printStackTrace();
					// Toast.makeText(getActivity(),
					// R.string.error_retrieve_topten,
					// Toast.LENGTH_SHORT).show();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				setupAdapter();
			}
		};

		fetchTopTenTask.execute();
		setupAdapter();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (mTopTen == null)
			return;

		Topic topic = mTopTen.get(position);
		Intent i = new Intent(getActivity(), ReplyListActivity.class);
		i.putExtra(ReplyListFragment.EXTRA_TOPIC, topic);
		startActivity(i);
	}

	private void setupAdapter() {
		if (getActivity() == null)
			return;
		if (mTopTen != null) {
			TopTenAdapter adapter = new TopTenAdapter(mTopTen);
			setListAdapter(adapter);
		} else {
			setListAdapter(null);
		}
	}

	private class TopTenAdapter extends ArrayAdapter<Topic> {
		public TopTenAdapter(ArrayList<Topic> topics) {
			super(getActivity(), 0, topics);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.list_item_topic, parent, false);
			}
			Topic topic = getItem(position);
			TextView textviewBoard = (TextView) convertView
					.findViewById(R.id.textview_board);
			textviewBoard.setText("[ " + topic.getBoard() + " ]");
			TextView textviewTitle = (TextView) convertView
					.findViewById(R.id.textview_title);
			textviewTitle.setText(topic.getTitle());
			return convertView;
		}
	}
}
