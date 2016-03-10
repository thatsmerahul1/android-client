package com.ecarezone.android.patient.fragment;

import com.ecarezone.android.patient.app.AbstractBaseFragment;
import com.ecarezone.android.patient.service.RoboEcareSpiceServices;
import com.octo.android.robospice.SpiceManager;

/**
 * Created by CHAO on 5/1/2015.
 */
public abstract class EcareZoneBaseFragment extends AbstractBaseFragment {
    private SpiceManager spiceManager = new SpiceManager(RoboEcareSpiceServices.class);

    public SpiceManager getSpiceManager() {
        if (!spiceManager.isStarted()) {
            spiceManager.start(getActivity());
        }
        return spiceManager;
    }

    //Starting the spice manager service fot robospice request
    @Override
    public void onStart() {
        super.onStart();
        if (!spiceManager.isStarted()) {
            spiceManager.start(getActivity());
        }

    }

    //Stoping spice manager service
    @Override
    public void onStop() {
        super.onStop();
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
    }
}
