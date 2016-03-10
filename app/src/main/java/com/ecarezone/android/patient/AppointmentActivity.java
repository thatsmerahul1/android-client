package com.ecarezone.android.patient;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ecarezone.android.patient.fragment.AppointmentFragment;

/**
 * Created by L&T Technology Services.
 */
public class AppointmentActivity extends EcareZoneBaseActivity {

    private Toolbar mToolBar = null;
    private ActionBar mActionBar;

    @Override
    protected String getCallerName() {
        return null;
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if (fragmentLayoutResId < 0) return;

        if (fragmentLayoutResId == R.layout.frag_appointment) {
            changeFragment(R.id.screen_container, new AppointmentFragment(),
                    AppointmentFragment.class.getSimpleName(), args);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_appointment);
        Bundle data = getIntent().getExtras();
        onNavigationChanged(R.layout.frag_appointment, ((data == null) ? null : data));
        mToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mToolBar.setNavigationIcon(R.drawable.ic_action_menu);
        }
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        addSupportOnBackStackChangedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
