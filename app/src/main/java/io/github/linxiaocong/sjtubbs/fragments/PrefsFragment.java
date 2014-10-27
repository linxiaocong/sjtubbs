package io.github.linxiaocong.sjtubbs.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.utilities.BBSUtils;

/**
 * Created by linxiaocong on 2014/10/21.
 */
public class PrefsFragment extends PreferenceFragment {

    private SharedPreferences mSharedPreferences;
    private static final String tag = "PrefsFragment";

    PreferenceFragment  newInstance() {
        return new PrefsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                String username = sharedPreferences.getString("username", "");
                String password = sharedPreferences.getString("password", "");
                Log.d(tag, "Username: " + username);
                if (!username.equals("") && !password.equals("")) {
                    AsyncTask<String, Void, Boolean> loginTask = new AsyncTask<String, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(String... params) {
                            return BBSUtils.getInstance().login(params[0], params[1]);
                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                            if (result == Boolean.TRUE) {
                                Toast.makeText(getActivity(), R.string.info_login_successfully,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), R.string.error_wrong_password,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    loginTask.execute(username, password);
                }
            }
        });
    }
}
