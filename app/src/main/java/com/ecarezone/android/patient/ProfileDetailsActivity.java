package com.ecarezone.android.patient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.fragment.UserProfileDetailsFragment;
import com.ecarezone.android.patient.utils.Util;

/**
 * Created by CHAO WEI on 5/31/2015.
 */
public class ProfileDetailsActivity extends EcareZoneBaseActivity {

    private ActionBar mActionBar = null;
    private Toolbar mToolBar = null;
    private UserProfileDetailsFragment userProfileDetailsFragment = new UserProfileDetailsFragment();

    public static String IS_NEW_PROFILE = "is_new_profile";
    public static String PROFILE_ID = "profile_id";

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
        setSupportActionBar(mToolBar);
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        addSupportOnBackStackChangedListener(this);
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_check, menu);
        return true;
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
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if (fragmentLayoutResId < 0) return;

        if (fragmentLayoutResId == R.layout.frag_user_profle_details) {
            changeFragment(R.id.screen_container, userProfileDetailsFragment,
                    UserProfileDetailsFragment.class.getSimpleName(), args);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        userProfileDetailsFragment.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        userProfileDetailsFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.changeStatus(Constants.ONLINE, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.changeStatus(Constants.IDLE, this);
    }
}
