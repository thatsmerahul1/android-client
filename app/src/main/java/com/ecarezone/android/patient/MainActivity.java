package com.ecarezone.android.patient;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.fragment.DoctorListFragment;
import com.ecarezone.android.patient.fragment.FirstTimeUserProfileFragment;
import com.ecarezone.android.patient.fragment.NewsCategoriesFragment;
import com.ecarezone.android.patient.fragment.PatientFragment;
import com.ecarezone.android.patient.fragment.SettingsFragment;
import com.ecarezone.android.patient.fragment.UserProfileFragment;
import com.ecarezone.android.patient.fragment.WelcomeFragment;
import com.ecarezone.android.patient.model.database.ProfileDbApi;

/**
 * Created by CHAO WEI on 5/3/2015.
 */
public class MainActivity extends EcareZoneBaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout mDrawerLayout = null;
    private ActionBarDrawerToggle mDrawerToggle = null;
    private Toolbar mToolBar = null;
    private ActionBar mActionBar = null;
    private boolean isBackStackRequired;
    private boolean isWelcomeMainRequired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        if (mDrawerLayout == null) {
            mDrawerLayout = (DrawerLayout) findViewById(R.id.side_drawer_layout);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    invalidateOptionsMenu();
                    syncState();
                }

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    invalidateOptionsMenu();
                    syncState();
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerLayout.setFitsSystemWindows(true);
            mDrawerToggle.syncState();
            mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    mDrawerToggle.syncState();
                }
            });
        }

        mToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mToolBar.setNavigationIcon(R.drawable.ic_action_menu);
        }
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(Constants.ECARE_ZONE);

        /* queries the db and checks whether to show welcome screen or to show home screen.
           Check is based on whether the user has created a profile or not. */
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                ProfileDbApi profileDbApi = new ProfileDbApi(getApplicationContext());
                boolean hasProfiles = profileDbApi.hasProfile(LoginInfo.userId.toString());
                return hasProfiles;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    onNavigationChanged(R.layout.frag_patient_main, null);
                } else {
                    onNavigationChanged(R.layout.frag_welcome, null);
                }
                isWelcomeMainRequired = aBoolean;
                super.onPostExecute(aBoolean);
            }
        }.execute();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    public void toggleDrawer(boolean open) {
        if (mDrawerLayout != null) {
            if (open) {
                mDrawerLayout.openDrawer(Gravity.START | Gravity.LEFT);
            } else {
                mDrawerLayout.closeDrawers();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ((mDrawerToggle != null) && (mDrawerToggle.onOptionsItemSelected(item))) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerOpen(Gravity.START | Gravity.LEFT)) {
                mDrawerLayout.closeDrawers();
                return;
            }
        }
        if (isBackStackRequired) {
            onNavigationChanged(isWelcomeMainRequired ? R.layout.frag_patient_main : R.layout.frag_welcome, null);
            isBackStackRequired = false;
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected String getCallerName() {
        return MainActivity.class.getSimpleName();
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        Log.d(TAG, "onNavigationChanged " + fragmentLayoutResId);
        if (fragmentLayoutResId < 0) return;
        if (fragmentLayoutResId == R.layout.frag_patient_main) {
            changeFragment(R.id.screen_container, new PatientFragment(),
                    getString(R.string.main_side_menu_home), args, false);
            isBackStackRequired = false;
            isWelcomeMainRequired = true;
        } else if (fragmentLayoutResId == R.layout.frag_welcome) {
            changeFragment(R.id.screen_container, new WelcomeFragment(),
                    WelcomeFragment.class.getSimpleName(), args, false);
            isBackStackRequired = false;
        } else if (fragmentLayoutResId == R.layout.frag_news_categories) {
            changeFragment(R.id.screen_container, new NewsCategoriesFragment(),
                    getString(R.string.main_side_menu_news), args, false);
            isBackStackRequired = true;
        } else if (fragmentLayoutResId == R.layout.frag_doctor_list) {
            changeFragment(R.id.screen_container, new DoctorListFragment(),
                    getString(R.string.main_side_menu_doctors), args, false);
            isBackStackRequired = true;
        } else if (fragmentLayoutResId == R.layout.frag_settings) {
            changeFragment(R.id.screen_container, new SettingsFragment(),
                    getString(R.string.main_side_menu_settings), args, false);
            isBackStackRequired = true;
        } else if (fragmentLayoutResId == R.layout.list_view) {
            changeFragment(R.id.screen_container, new UserProfileFragment(),
                    UserProfileFragment.class.getSimpleName(), args, false);
            isBackStackRequired = true;
        } else if (fragmentLayoutResId == R.layout.frag_first_time_profile) {
            changeFragment(R.id.screen_container, new FirstTimeUserProfileFragment(),
                    FirstTimeUserProfileFragment.class.getSimpleName(), args, false);
            isBackStackRequired = true;
        }

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawers();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onServiceConnected() {

    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
