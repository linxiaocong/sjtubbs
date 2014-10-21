package io.github.linxiaocong.sjtubbs.activities;

import io.github.linxiaocong.sjtubbs.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class SingleFragmentActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean flag = true;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return flag;
    }
}
