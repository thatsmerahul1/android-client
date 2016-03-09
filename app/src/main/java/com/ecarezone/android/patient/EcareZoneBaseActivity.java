package com.ecarezone.android.patient;

import android.support.v4.app.FragmentManager;

import com.ecarezone.android.patient.app.AbstractBaseActivity;



public abstract class EcareZoneBaseActivity extends AbstractBaseActivity implements AbstractBaseActivity.OnNavigationChangedListener,
                                                                                    FragmentManager.OnBackStackChangedListener {


    @Override
    public void onBackStackChanged() {
        final int entryCount = getFragmentBackStackEntryCount();
        if(entryCount == 0) {
            finish();
        }
    }
}
