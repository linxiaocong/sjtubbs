package io.github.linxiaocong.sjtubbs.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.fragments.PrefsFragment;

/**
 * Created by linxiaocong on 2014/10/21.
 */
public class PrefsActivity extends ActionBarActivity {

    private static final String tag = "PrefsActivity";

    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_without_margin);
        setTitle(R.string.action_settings);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PrefsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
