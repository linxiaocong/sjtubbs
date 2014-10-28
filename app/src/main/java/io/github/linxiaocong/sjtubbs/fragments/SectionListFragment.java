package io.github.linxiaocong.sjtubbs.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.activities.TopicListActivity;
import io.github.linxiaocong.sjtubbs.dao.SectionDAO;
import io.github.linxiaocong.sjtubbs.models.Board;
import io.github.linxiaocong.sjtubbs.models.Section;

/**
 * Created by linxiaocong on 2014/9/26.
 */
public class SectionListFragment extends Fragment {

    private ExpandableListView mSectionListView = null;
    private ArrayList<Section> mSections = null;

    private static final String tag = "SectionListFragment";

    public static SectionListFragment newInstance() {
        return new SectionListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_section_list, container,
                false);
        mSectionListView = (ExpandableListView) view
                .findViewById(R.id.section_list);
        mSectionListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Board board = mSections.get(groupPosition).getBoards().get(childPosition);

                if (board.hasSubBoard()) {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.dialog_board_list);
                    dialog.setTitle(R.string.sub_boards_dialog_title);
                    ListView listView = (ListView) dialog.findViewById(R.id.board_list);
                    ArrayAdapter<Board> adapter = new ArrayAdapter<Board>(getActivity(), 0, board.getSubBoards()) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            if (convertView == null) {
                                convertView = getActivity().getLayoutInflater().inflate(
                                        R.layout.list_item_board, parent, false);
                            }
                            Board board = getItem(position);
                            TextView textView = (TextView) convertView.findViewById(R.id.textview_board);
                            textView.setText(board.getDesc() + " " + board.getName());
                            return convertView;
                        }
                    };
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Board selectedBoard = (Board) adapterView.getAdapter().getItem(i);
                            Log.d(tag, "The selected board is: " + selectedBoard.getUrl());
                            startTopicListActivity(selectedBoard);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    startTopicListActivity(board);
                }
                return false;
            }
        });

        AsyncTask<Void, Void, Void> fetchSectionsTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mSections = (new SectionDAO(getActivity())).getSections();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                setupAdapter();
            }
        };
        fetchSectionsTask.execute();
        setupAdapter();
        return view;
    }

    private void setupAdapter() {
        if (getActivity() == null)
            return;
        if (mSections == null) {
            return;
        } else {
            mSectionListView.setAdapter(new SectionListAdapter());
        }
    }

    private void startTopicListActivity(Board board) {
        Intent intent = new Intent(getActivity(), TopicListActivity.class);
        intent.putExtra(TopicListFragment.EXTRA_BOARD, board);
        startActivity(intent);
    }

    class SectionListAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return mSections == null ? 0 : mSections.size();
        }

        @Override
        public int getChildrenCount(int i) {
            if (mSections == null)
                return 0;
            return mSections.get(i).getBoards().size();
        }

        @Override
        public Object getGroup(int i) {
            if (mSections == null)
                return null;
            return mSections.get(i);
        }

        @Override
        public Object getChild(int i, int i2) {
            if (mSections == null)
                return null;
            return mSections.get(i).getBoards().get(i);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i2) {
            return i2;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view,
                                 ViewGroup viewGroup) {
            if (view == null) {
                view = getActivity().getLayoutInflater().inflate(
                        R.layout.list_item_section, viewGroup, false);
            }
            ((TextView) view).setText(mSections.get(i).getName());
            return view;
        }

        @Override
        public View getChildView(int i, int i2, boolean b, View view,
                                 ViewGroup viewGroup) {
            if (view == null) {
                view = getActivity().getLayoutInflater().inflate(
                        R.layout.list_item_board, viewGroup, false);
            }
            Board board = mSections.get(i).getBoards().get(i2);
            ((TextView) view).setText(board.getDesc() + " " + board.getName());
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i2) {
            return true;
        }
    }
}
