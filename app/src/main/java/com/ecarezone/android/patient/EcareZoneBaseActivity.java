package com.ecarezone.android.patient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;

import com.ecarezone.android.patient.app.AbstractBaseActivity;
import com.ecarezone.android.patient.service.RoboEcareSpiceServices;
import com.ecarezone.android.patient.service.SinchService;
import com.ecarezone.android.patient.utils.SinchUtil;
import com.octo.android.robospice.SpiceManager;

/**
 * Created by L&T Technology Services
 */
public abstract class EcareZoneBaseActivity extends AbstractBaseActivity implements AbstractBaseActivity.OnNavigationChangedListener,
        FragmentManager.OnBackStackChangedListener, ServiceConnection {
    private SinchService.SinchServiceInterface mSinchServiceInterface;

    private SpiceManager spiceManager = new SpiceManager(RoboEcareSpiceServices.class);

    public SpiceManager getSpiceManager() {
        if (!spiceManager.isStarted()) {
            spiceManager.start(this);
        }
        return spiceManager;
    }

    @Override
    public void onBackStackChanged() {
        final int entryCount = getFragmentBackStackEntryCount();
        if (entryCount == 0) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            SinchUtil.setSinchServiceInterface((SinchService.SinchServiceInterface) iBinder);
        }
        onServiceConnected();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            SinchUtil.setSinchServiceInterface(null);
            onServiceDisconnected();
        }
    }

    public void stopSinchService(){
        SinchUtil.getSinchServiceInterface().stopClient();
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }
}
