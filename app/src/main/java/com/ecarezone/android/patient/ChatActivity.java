package com.ecarezone.android.patient;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ecarezone.android.patient.fragment.DoctorFragment;

/**
 * Created by CHAO WEI on 6/1/2015.
 */
public class ChatActivity extends EcareZoneBaseActivity  {

    private ActionBar mActionBar = null;
    private Toolbar mToolBar = null;

    @Override
    protected String getCallerName() {
        return ChatActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chat);
        Bundle data = getIntent().getExtras();
        onNavigationChanged(R.layout.frag_chat, ((data == null) ? null : data));
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if(fragmentLayoutResId < 0) return;

        /*if(fragmentLayoutResId == R.layout.frag_chat) {
            changeFragment(R.id.screen_container, new ChatFragment(),
                    ChatFragment.class.getSimpleName(), args);
        }*/
    }
}
