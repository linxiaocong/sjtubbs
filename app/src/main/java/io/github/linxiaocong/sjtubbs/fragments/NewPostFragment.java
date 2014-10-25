package io.github.linxiaocong.sjtubbs.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.models.Board;
import io.github.linxiaocong.sjtubbs.utilities.BBSUtils;

/**
 * Created by linxiaocong on 2014/10/22.
 */
public class NewPostFragment extends Fragment {

    public static final String EXTRA_BOARD = "extra_board";

    private final int REQUEST_CODE_GALLERY = 1;
    private final String tag = "NewPostFragment";

    private EditText mEditTextTitle;
    private EditText mEditTextContent;
    private Board mBoard;

    public static NewPostFragment newInstance(Board board) {
        NewPostFragment fragment = new NewPostFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_BOARD, board);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mBoard = (Board)getArguments().getSerializable(EXTRA_BOARD);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        AsyncTask<String, Void, Void> loginTask = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                BBSUtils.getInstance().login(params[0], params[1]);
                return null;
            }
        };
        String username = prefs.getString(getResources().getString(R.string.key_username),
                "");
        String password = prefs.getString(getResources().getString(R.string.key_password),
                "");
        loginTask.execute(username, password);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_post, container, false);
        mEditTextTitle = (EditText)view.findViewById(R.id.editText_title);
        mEditTextContent = (EditText)view.findViewById(R.id.editText_content);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_new_post, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_upload:
                Intent intent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
                return true;
            case R.id.action_send:
                (new PostTask()).execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_GALLERY) {
            Uri[] photoUris;
            ClipData clipData = data.getClipData();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && clipData != null) {
                photoUris = new Uri[clipData.getItemCount()];
                for (int i = 0; i < clipData.getItemCount(); ++i) {
                    photoUris[i] = clipData.getItemAt(i).getUri();
                }
            } else {
                photoUris = new Uri[1];
                photoUris[0] = data.getData();
            }
            for (Uri uri: photoUris) {
                (new UploadTask()).execute(uri);
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class UploadTask extends AsyncTask<Uri, Void, String> {
        @Override
        protected String doInBackground(Uri... params) {
            return BBSUtils.getInstance().uploadPicture(getActivity(), params[0], mBoard.getName());
        }
        @Override
        protected void onPostExecute(String result) {
            Log.d(tag, "uploaded file to: " + result);
            String content = mEditTextContent.getText().toString();
            content = content + "\n" + result;
            mEditTextContent.setText(content);
        }
    }

    class PostTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("signature", "1"));
            nameValuePairs.add(new BasicNameValuePair("autocr", "on"));
            nameValuePairs.add(new BasicNameValuePair("up", ""));
            nameValuePairs.add(new BasicNameValuePair("MAX_FILE_SIZE", "1048577"));
            nameValuePairs.add(new BasicNameValuePair("level", "0"));
            nameValuePairs.add(new BasicNameValuePair("live", "180"));
            nameValuePairs.add(new BasicNameValuePair("exp", "0"));
            nameValuePairs.add(new BasicNameValuePair("board", mBoard.getName()));
            nameValuePairs.add(new BasicNameValuePair("file", ""));
            nameValuePairs.add(new BasicNameValuePair("reidstr", ""));
            nameValuePairs.add(new BasicNameValuePair("reply_to_user", ""));
            nameValuePairs.add(new BasicNameValuePair("title", mEditTextTitle.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("text", mEditTextContent.getText().toString()));
            return BBSUtils.getInstance().post(nameValuePairs);
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(getActivity(), R.string.info_post_successfully, Toast.LENGTH_SHORT)
                        .show();
                getActivity().onBackPressed();
            } else {
                Toast.makeText(getActivity(), R.string.error_post, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
