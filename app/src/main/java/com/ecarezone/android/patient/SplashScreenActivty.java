package com.ecarezone.android.patient;

import android.os.Bundle;
import android.view.Window;

//import com.crittercism.app.Crittercism;
import com.ecarezone.android.patient.fragment.SplashScreenFragment;
import com.ecarezone.android.patient.utils.SinchUtil;


public class SplashScreenActivty extends EcareZoneBaseActivity {

    SplashScreenFragment splashScreenFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);

//        Crittercism.initialize(getApplicationContext(),
//                "56b49f2fb35f950b00e1ad37");
        splashScreenFragment = SplashScreenFragment.newInstance();
        onNavigationChanged(R.layout.frag_splashscreen, null);
        addSupportOnBackStackChangedListener(this);
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if (fragmentLayoutResId == R.layout.frag_splashscreen) {

            changeFragment(R.id.screen_container, splashScreenFragment,
                    SplashScreenFragment.class.getSimpleName(), args);
        }
    }

    @Override
    protected String getCallerName() {
        return SplashScreenActivty.class.getSimpleName();
    }

    @Override
    public void onServiceConnected() {
        SinchUtil.getSinchServiceInterface().setStartListener(splashScreenFragment);
    }

    @Override
    public void onServiceDisconnected() {

    }

}
