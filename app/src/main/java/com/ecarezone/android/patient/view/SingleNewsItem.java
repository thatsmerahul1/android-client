package com.ecarezone.android.patient.view;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecarezone.android.patient.EcareZoneBaseActivity;
import com.ecarezone.android.patient.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Namitha on 6/16/2016.
 */
public class SingleNewsItem extends EcareZoneBaseActivity {

    private ActionBar mActionBar = null;
    private Toolbar mToolBar = null;

    @Override
    protected String getCallerName() {
        return SingleNewsItem.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_single_item);

        ImageView image = (ImageView)findViewById(R.id.news_image_item);
        TextView description = (TextView)findViewById(R.id.news_description_item);
        mToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolBar);
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setElevation(20);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        Bundle data = getIntent().getBundleExtra("bundle");
        description.setText(data.getString("description"));
        String imgPath = data.getString("news_link");
        Picasso.with(this)
                    .load(imgPath)
                    .into(image);
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {

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


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}
