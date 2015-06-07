package com.ecarezone.android.patient;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ecarezone.android.patient.fragment.DoctorFragment;

/**
 * Created by CHAO WEI on 6/1/2015.
 */
public class DoctorActivity extends EcareZoneBaseActivity  {

    private ActionBar mActionBar = null;
    private Toolbar mToolBar = null;

    @Override
    protected String getCallerName() {
        return DoctorActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_doctor);
        onNavigationChanged(R.layout.frag_doctor, null);
        mToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mToolBar.setNavigationIcon(R.drawable.ic_action_menu);
            mToolBar.setOnMenuItemClickListener(
                    new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // Handle menu item click event
                            return true;
                        }
                    });
        }
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        addSupportOnBackStackChangedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public void onBackStackChanged() {
        final int entryCount = getFragmentBackStackEntryCount();
        if(entryCount == 0) {
            finish();
        }
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if(fragmentLayoutResId < 0) return;

        if(fragmentLayoutResId == R.layout.frag_doctor) {
            changeFragment(R.id.screen_container, new DoctorFragment(),
                    DoctorFragment.class.getSimpleName(), args);
        }
    }
}
