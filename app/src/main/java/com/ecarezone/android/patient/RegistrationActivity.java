package com.ecarezone.android.patient;

import android.os.Bundle;

import com.ecarezone.android.patient.fragment.LoginFragment;
import com.ecarezone.android.patient.fragment.RegistrationFragment;

/**
 * Created by CHAO WEI on 5/10/2015.
 */
public class RegistrationActivity extends EcareZoneBaseActivity {

    @Override
    protected String getCallerName() {
        return RegistrationActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_registraion);
        onNavigationChanged(R.layout.frag_login, null);
        addSupportOnBackStackChangedListener(this);
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if(fragmentLayoutResId < 0) return;

        if(fragmentLayoutResId == R.layout.frag_login) {
            changeFragment(R.id.screen_container, LoginFragment.newInstance(),
                    LoginFragment.class.getSimpleName(), args);
        } else if (fragmentLayoutResId == R.layout.frag_registration) {
            changeFragment(R.id.screen_container, RegistrationFragment.newInstance(),
                    RegistrationFragment.class.getSimpleName(), args);
        }
    }

    @Override
    public void onBackStackChanged() {
        final int entryCount = getFragmentBackStackEntryCount();
        if(entryCount == 0) {
            finish();
        }
    }
}
