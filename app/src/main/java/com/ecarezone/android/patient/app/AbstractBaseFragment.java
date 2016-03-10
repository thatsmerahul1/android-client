package com.ecarezone.android.patient.app;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by CHAO WEI on 5/1/2015.
 */
public abstract class AbstractBaseFragment extends Fragment {

    private AbstractBaseActivity.OnNavigationChangedListener mListener = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AbstractBaseActivity.OnNavigationChangedListener) activity;
        } catch (Exception e) {
        }
    }

    /**
     * @return caller Class name
     */
    protected abstract String getCallerName();

    protected void invokeNavigationChanged(int layoutResId, Bundle bundle) {
        if (mListener != null) {
            mListener.onNavigationChanged(layoutResId, bundle);
        }
    }

    protected Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    /**
     * a shortcut for getSupportFragmentManager()#findFragmentById(resId)
     *
     * @param resId
     * @return Fragment
     */
    protected Fragment getFragmentById(final int resId) {
        return (Fragment) getFragmentManager().findFragmentById(resId);
    }

}
