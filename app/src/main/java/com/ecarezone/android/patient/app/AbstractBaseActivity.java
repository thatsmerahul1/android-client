package com.ecarezone.android.patient.app;


import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by CHAO WEI on 5/1/2015.
 */

public abstract class AbstractBaseActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, ServiceConnection {


    protected FragmentTransaction getSupportFragmentTransaction() {
        return getSupportFragmentManager().beginTransaction();
    }

    /**
     * Add support OnBackStackChangedListener
     *
     * @param listener {@link FragmentManager.OnBackStackChangedListener}
     */
    protected void addSupportOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener listener) {
        getSupportFragmentManager().addOnBackStackChangedListener(listener);
    }


    protected void popBackStack() {
        getSupportFragmentManager().popBackStack();
    }

    /**
     * @return caller Class name
     */
    protected abstract String getCallerName();


    /**
     * a shortcut for getSupportFragmentManager()#getBackStackEntryCount()
     *
     * @return total entry count
     */
    protected final int getFragmentBackStackEntryCount() {
        return getSupportFragmentManager().getBackStackEntryCount();
    }

    protected <T extends Fragment> void changeFragment(final int containerResId, T fragment, String tag, Bundle args) {
        changeFragment(containerResId, fragment, tag, args, true);
    }

    /**
     * Change the fragment for the current activity.
     *
     * @param containerResId is the resource id of the screen container
     * @param fragment       is an instance of act_splash subclass of android.support.v4.app.Fragment
     * @param tag            is act_splash string title that indicates the fragment
     * @param args           is act_splash container for passing objects
     * @param addToStack     is indicating adding the fragment to back stack, if needed
     */
    protected <T extends Fragment> void changeFragment(final int containerResId, T fragment, String tag, Bundle args, boolean addToStack) {
        FragmentTransaction transaction = getSupportFragmentTransaction();
        if (args != null) {
            fragment.setArguments(args);
        }
        transaction.replace(containerResId, fragment, tag);
        if (addToStack) {
            // add to this transaction into BackStack
            transaction.addToBackStack(tag);
        }
        // add the animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        // commit this transaction
        transaction.commit();
    }


    ///////////////////////////////////////////////////////////////////////////

    /**
     * Interface for navigation change
     */
    public static interface OnNavigationChangedListener {
        /**
         * callback method for fragment change
         *
         * @param fragmentLayoutResId
         * @param args
         */
        void onNavigationChanged(int fragmentLayoutResId, Bundle args);
    }

}
