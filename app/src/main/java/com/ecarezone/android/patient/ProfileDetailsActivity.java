package com.ecarezone.android.patient;

import android.os.Bundle;

import com.ecarezone.android.patient.fragment.UserProfileDetailsFragment;
import com.ecarezone.android.patient.fragment.UserProfileFragment;

/**
 * Created by CHAO WEI on 5/31/2015.
 */
public class ProfileDetailsActivity extends EcareZoneBaseActivity {
    @Override
    protected String getCallerName() {
        return ProfileDetailsActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_profile);
        onNavigationChanged(R.layout.frag_user_profle_details, null);
        addSupportOnBackStackChangedListener(this);
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
