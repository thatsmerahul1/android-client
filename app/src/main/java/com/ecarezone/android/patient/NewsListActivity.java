package com.ecarezone.android.patient;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ecarezone.android.patient.fragment.NewsCategoriesFragment;
import com.ecarezone.android.patient.fragment.NewsListFragment;

/**
 * Created by CHAO WEI on 5/31/2015.
 */
public class NewsListActivity extends EcareZoneBaseActivity{

    private ActionBar mActionBar = null;
    private Toolbar mToolBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_news_list);
        onNavigationChanged(R.layout.list_view, getIntent().getBundleExtra(NewsCategoriesFragment.NEWS_BUNDLE));

        mToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolBar);
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        addSupportOnBackStackChangedListener(this);
    }

    @Override
    protected String getCallerName() {
        return NewsListActivity.class.getSimpleName();
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if(fragmentLayoutResId < 0) return;

        if(fragmentLayoutResId == R.layout.list_view) {
            NewsListFragment newsListFragment = new NewsListFragment();

            changeFragment(R.id.screen_container, newsListFragment ,
                    NewsListFragment.class.getSimpleName(), args);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
