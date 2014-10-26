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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.utilities.BBSUtils;

/**
 * Created by linxiaocong on 2014/10/22.
 */
public class NewPostFragment extends Fragment {

    public static final String EXTRA_BOARD_NAME = "extra_board_name";
    public static final String EXTRA_IS_REPLY = "extra_is_reply";
    public static final String EXTRA_REPLY_URL = "extra_reply_url";
    public static final String EXTRA_REPLY_TO = "extra_reply_to";

    private final int REQUEST_CODE_GALLERY = 1;
    private final String tag = "NewPostFragment";

    private EditText mEditTextTitle;
    private EditText mEditTextContent;

    ArrayList<NameValuePair> mNameValuePairs;
    private String mBoardName;
    private boolean mIsReply;
    private String mReplyTo;
    private String mReplyUrl;
    private String mUsername;
    private String mPassword;
    private String mSignature;

    public static NewPostFragment newInstance(Bundle args) {
        NewPostFragment fragment = new NewPostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mBoardName = getArguments().getString(EXTRA_BOARD_NAME);
        mIsReply = getArguments().getBoolean(EXTRA_IS_REPLY);
        if (mIsReply) {
            mReplyTo = getArguments().getString(EXTRA_REPLY_TO);
            mReplyUrl = getArguments().getString(EXTRA_REPLY_URL);
            (new GetReplyDataTask()).execute(mReplyUrl);
        }
        mNameValuePairs = new ArrayList<NameValuePair>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUsername = prefs.getString(getResources().getString(R.string.key_username),
                "");
        mPassword = prefs.getString(getResources().getString(R.string.key_password),
                "");
        mSignature = prefs.getString(getResources().getString(R.string.key_signature), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_post, container, false);
        mEditTextTitle = (EditText)view.findViewById(R.id.editText_title);
        mEditTextContent = (EditText)view.findViewById(R.id.editText_content);
        mEditTextContent.setText("\n" + mSignature);
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
                mNameValuePairs.add(new BasicNameValuePair("signature", "1"));
                mNameValuePairs.add(new BasicNameValuePair("autocr", "on"));
                mNameValuePairs.add(new BasicNameValuePair("up", ""));
                mNameValuePairs.add(new BasicNameValuePair("MAX_FILE_SIZE", "1048577"));
                mNameValuePairs.add(new BasicNameValuePair("level", "0"));
                mNameValuePairs.add(new BasicNameValuePair("live", "180"));
                mNameValuePairs.add(new BasicNameValuePair("exp", "0"));
                mNameValuePairs.add(new BasicNameValuePair("board", mBoardName));
                mNameValuePairs.add(new BasicNameValuePair("title", mEditTextTitle.getText().toString()));
                mNameValuePairs.add(new BasicNameValuePair("text", mEditTextContent.getText().toString()));
                if (!mIsReply) {
                    mNameValuePairs.add(new BasicNameValuePair("file", ""));
                    mNameValuePairs.add(new BasicNameValuePair("reidstr", ""));
                    mNameValuePairs.add(new BasicNameValuePair("reply_to_user", ""));
                } else {
                    mNameValuePairs.add(new BasicNameValuePair("reply_to_user", mReplyTo));
                }
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
            BBSUtils.getInstance().login(mUsername, mPassword);
            return BBSUtils.getInstance().uploadPicture(getActivity(), params[0], mBoardName);
        }
        @Override
        protected void onPostExecute(String result) {
            Log.d(tag, "uploaded file to: " + result);
            String content = mEditTextContent.getText().toString();
            content = result + "\n" + content;
            mEditTextContent.setText(content);
        }
    }

    class GetReplyDataTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            String[] results = new String[2];  // resutls[0] for title
                                                // results[1] for content
            Log.d(tag, "ReplyUrl is: " + params[0]);
            BBSUtils.getInstance().login(mUsername, mPassword);
            try {
                Document document = Jsoup.connect(params[0])
                        .cookies(BBSUtils.getInstance().getCookiesMap()).get();
                Elements elements = document.getElementsByTag("input");
                for (Element element : elements) {
                    if (element.attr("name").equals("file")) {
                        mNameValuePairs.add(new BasicNameValuePair("file", element.attr("value")));
                    } else if (element.attr("name").equals("reidstr")) {
                        mNameValuePairs.add(new BasicNameValuePair("reidstr", element.attr("value")));
                    } else if (element.attr("name").equals("title")) {
                        results[0] = element.attr("value");
                    }
                }
                Element textArea = document.select("#text").first();
                results[1] = textArea.text();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return results;
        }
        @Override
        protected void onPostExecute(String[] results) {
            if (results != null) {
                mEditTextTitle.setText(results[0]);
                mEditTextContent.setText(mEditTextContent.getText() + "\n" + results[1]);
            }
        }
    }

    class PostTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            BBSUtils.getInstance().login(mUsername, mPassword);
            return BBSUtils.getInstance().post(mNameValuePairs);
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
