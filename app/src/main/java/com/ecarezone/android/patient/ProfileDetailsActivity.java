package com.ecarezone.android.patient;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ecarezone.android.patient.fragment.UserProfileDetailsFragment;

/**
 * Created by CHAO WEI on 5/31/2015.
 */
public class ProfileDetailsActivity extends EcareZoneBaseActivity {

    private ActionBar mActionBar = null;
    private Toolbar mToolBar = null;

    @Override
    protected String getCallerName() {
        return ProfileDetailsActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_profile);
        onNavigationChanged(R.layout.frag_user_profle_details, null);
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
            popBackStack();
            //finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

         if(fragmentLayoutResId == R.layout.frag_user_profle_details) {
             changeFragment(R.id.screen_container, new UserProfileDetailsFragment(),
                     UserProfileDetailsFragment.class.getSimpleName(), args);
        }

    }
}
