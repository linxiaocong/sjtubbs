package io.github.linxiaocong.sjtubbs.activities;

import android.os.Bundle;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.fragments.PrefsFragment;

/**
 * Created by linxiaocong on 2014/10/21.
 */
public class PrefsActivity extends SingleFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.action_settings);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, (new PrefsFragment()))
                .commit();
    }
}
