package io.github.linxiaocong.sjtubbs.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import io.github.linxiaocong.sjtubbs.activities.ReplyListActivity;
import io.github.linxiaocong.sjtubbs.dao.TopTenDAO;
import io.github.linxiaocong.sjtubbs.models.Topic;

public class TopTenFragment extends Fragment {

    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

	private ArrayList<Topic> mTopTen;

	public static TopTenFragment newInstance() {
		TopTenFragment fragment = new TopTenFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TopTenAdapter adapter = (TopTenAdapter) mListView.getAdapter();
                try {
                    mTopTen.clear();
                    adapter.notifyDataSetChanged();
                    (new FetchTopTenTask()).execute();
                    mSwipeRefreshLayout.setRefreshing(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mListView = (ListView) view.findViewById(R.id.list_view);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mTopTen == null)
                    return;
                Topic topic = mTopTen.get(i);
                Intent intent = new Intent(getActivity(), ReplyListActivity.class);
                intent.putExtra(ReplyListFragment.EXTRA_TOPIC, topic);
                startActivity(intent);
            }
        });

        (new FetchTopTenTask()).execute();
        setupAdapter();

        return view;
    }

	private void setupAdapter() {
		if (getActivity() == null)
			return;
		if (mTopTen != null) {
			TopTenAdapter adapter = new TopTenAdapter(mTopTen);
            mListView.setAdapter(adapter);
		} else {
            mListView.setAdapter(null);
		}
	}

    private class FetchTopTenTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            TopTenDAO topTenDAO = new TopTenDAO(getActivity());
            try {
                mTopTen = topTenDAO.getTopTen();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            setupAdapter();
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
