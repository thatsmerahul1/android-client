package com.ecarezone.android.patient;

import android.os.Bundle;

import com.ecarezone.android.patient.fragment.SplashScreenFragment;

/**
 * Created by CHAO WEI on 5/1/2015.
 */
public class SplashScreenActivty extends EcareZoneBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        onNavigationChanged(R.layout.frag_splashscreen, null);
        addSupportOnBackStackChangedListener(this);
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if(fragmentLayoutResId == R.layout.frag_splashscreen) {
            changeFragment(R.id.screen_container, new SplashScreenFragment(), "", args);
        }
    }

    @Override
    public void onBackStackChanged() {
        final int entryCount = getFragmentBackStackEntryCount();
        if(entryCount == 0) {
            finish();
        }
    }


    @Override
    protected String getCallerName() {
        return SplashScreenActivty.class.getSimpleName();
    }

}
