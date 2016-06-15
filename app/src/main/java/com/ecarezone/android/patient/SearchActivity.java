package com.ecarezone.android.patient;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ecarezone.android.patient.fragment.SearchFragment;
import com.ecarezone.android.patient.utils.Util;

/**
 * Created by CHAO WEI on 6/19/2015.
 */
public class SearchActivity extends EcareZoneBaseActivity {

    private static final String TAG = SearchActivity.class.getSimpleName();
    private ActionBar mActionBar = null;
    private Toolbar mToolBar = null;

    @Override
    protected String getCallerName() {
        return SearchActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_doctor);
        onNavigationChanged(R.layout.frag_search_user_list, getIntent().getBundleExtra("doctorList"));
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
        mActionBar.setTitle(getResources().getString(R.string.main_side_menu_doctors));
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
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if (fragmentLayoutResId < 0) return;

        if (fragmentLayoutResId == R.layout.frag_search_user_list) {
            changeFragment(R.id.screen_container, new SearchFragment(),
                    SearchFragment.class.getSimpleName(), args);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.changeStatus(true, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.changeStatus(false, this);
    }
}
