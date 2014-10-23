package io.github.linxiaocong.sjtubbs.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.utilities.BBSUtils;

/**
 * Created by linxiaocong on 2014/10/21.
 */
public class PrefsActivity extends PreferenceActivity {

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.action_settings);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, (new PreferencesFragment()))
                .commit();
    }
    */

    private static final String tag = "PrefsActivity";
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                String username = sharedPreferences.getString(getResources().getString(R.string.key_username),
                        "");
                String password = sharedPreferences.getString(getResources().getString(R.string.key_password),
                        "");
                Log.d(tag, "Username: " + username);
                AsyncTask<String, Void, Boolean> loginTask = new AsyncTask<String, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(String... params) {
                        return BBSUtils.getInstance().login(params[0], params[1]);
                    }
                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (result == Boolean.TRUE) {
                            Toast.makeText(PrefsActivity.this, R.string.info_login_successfully,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PrefsActivity.this, R.string.error_wrong_password,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                loginTask.execute(username, password);
            }
        });
    }
}
