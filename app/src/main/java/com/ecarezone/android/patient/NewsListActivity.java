package com.ecarezone.android.patient;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.fragment.NewsCategoriesFragment;
import com.ecarezone.android.patient.fragment.NewsListFragment;
import com.ecarezone.android.patient.utils.Util;

/**
 * Created by CHAO WEI on 5/31/2015.
 */
public class NewsListActivity extends EcareZoneBaseActivity {

    private ActionBar mActionBar = null;
    private Toolbar mToolBar = null;
    private NewsListFragment newsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_news_list);

        Bundle bundle = getIntent().getBundleExtra(NewsCategoriesFragment.NEWS_BUNDLE);
        if (bundle == null) {
            // is redirected from the navigation bar item click
            bundle = new Bundle();
            bundle.putString(NewsCategoriesFragment.NEWS_CATEGORY_NAME,
                    getIntent().getStringExtra(NewsCategoriesFragment.NEWS_CATEGORY_NAME));
        }

        onNavigationChanged(R.layout.list_view, bundle);

        mToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(newsListFragment != null){
                    NavUtils.navigateUpFromSameTask(NewsListActivity.this);
//                }
            }
        });

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
        if (fragmentLayoutResId < 0) return;

        if (fragmentLayoutResId == R.layout.list_view) {
            newsListFragment = new NewsListFragment();
            changeFragment(R.id.screen_container, newsListFragment,
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
