package com.ecarezone.android.patient.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.app.widget.NavigationItem;

/**
 * Created by CHAO WEI on 5/3/2015.
 */
public class SideNavigationFragment extends EcareZoneBaseFragment implements NavigationItem.OnNavigationItemClickListener,
                                                                            View.OnClickListener,
                                                                            FragmentManager.OnBackStackChangedListener {

    @Override
    protected String getCallerName() {
        return SideNavigationFragment.class.getSimpleName();
    }

    private NavigationItem mHome = null;
    private NavigationItem mNews = null;
    private NavigationItem mHealth = null;
    private NavigationItem mDoctors = null;
    private NavigationItem mMedication = null;
    private NavigationItem mPlan = null;
    private NavigationItem mSettings = null;
    private NavigationItem mLogout = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_side_navigation, container, false);
        view.findViewById(R.id.navigation_user_profile).setOnClickListener(this);
        mHome =  (NavigationItem) view.findViewById(R.id.navigation_home);
        mHome.setOnNavigationItemClickListener(this);
        mNews =  (NavigationItem) view.findViewById(R.id.navigation_news);
        mNews.setOnNavigationItemClickListener(this);
        mHealth =  (NavigationItem) view.findViewById(R.id.navigation_health);
        mHealth.setOnNavigationItemClickListener(this);
        mHealth.setEnabled(false);
        mDoctors =  (NavigationItem) view.findViewById(R.id.navigation_doctors);
        mDoctors.setOnNavigationItemClickListener(this);
        mMedication =  (NavigationItem) view.findViewById(R.id.navigation_medication);
        mMedication.setOnNavigationItemClickListener(this);
        mMedication.setEnabled(false);
        mPlan =  (NavigationItem) view.findViewById(R.id.navigation_plan);
        mPlan.setOnNavigationItemClickListener(this);
        mPlan.setEnabled(false);
        mSettings =  (NavigationItem) view.findViewById(R.id.navigation_settings);
        mSettings.setOnNavigationItemClickListener(this);
        mLogout =  (NavigationItem) view.findViewById(R.id.navigation_logout);
        mLogout.setOnNavigationItemClickListener(this);
        mHome.highlightItem(true);
        return view;
    }

    @Override
    public void onItemClick(View v) {
        final String tag = String.valueOf(v.getTag());
        int layoutResId = 0;
        if(!TextUtils.isEmpty(tag)) {
            Bundle b = null;
            if(getString(R.string.main_side_menu_home).equals(tag)) {
                layoutResId = R.layout.frag_patient_main;
            } else if(getString(R.string.main_side_menu_news).equals(tag)) {
                layoutResId = R.layout.frag_news_categories;
            } else if(getString(R.string.main_side_menu_doctors).equals(tag)){
                layoutResId = R.layout.frag_doctors;
            } else if(getString(R.string.main_side_menu_health).equals(tag)) {
                // TODO
            } else if(getString(R.string.main_side_menu_medication).equals(tag)) {
                // TODO
            } else if(getString(R.string.main_side_menu_medication).equals(tag)) {
                // TODO
            }  else if(getString(R.string.main_side_menu_settings).equals(tag)) {
                layoutResId = R.layout.frag_settings;
            } else if (getString(R.string.main_side_menu_logout).equals(tag)) {
                // TODO
            }

            if(layoutResId > 0) {
                invokeNavigationChanged(layoutResId, b);
            }
        }
    }

    private void highlightNavigationItem(NavigationItem navigationItem) {
        mHome.highlightItem(false);
        //mHealth.highlightItem(false);
        mNews.highlightItem(false);
        mDoctors.highlightItem(false);
        //mMedication.highlightItem(false);
        //mPlan.highlightItem(false);
        mSettings.highlightItem(false);
        //mLogout.highlightItem(false);
        navigationItem.highlightItem(true);
    }

    @Override
    public void onClick(View v) {
        if( v == null) return;

        final int viewId = v.getId();
        if(viewId == R.id.navigation_user_profile) {
            invokeNavigationChanged(R.layout.frag_user_profile, null);
        }
    }

    @Override
    public void onBackStackChanged() {
        final Fragment fragment = getFragmentById(R.id.screen_container);
        if(fragment != null) {
            final String tag = fragment.getTag();
            if(getString(R.string.main_side_menu_doctors).equals(tag)) {
                highlightNavigationItem(mDoctors);
            } else if(getString(R.string.main_side_menu_home).equals(tag)) {
                highlightNavigationItem(mHome);
            } else if(getString(R.string.main_side_menu_news).equals(tag)) {
                highlightNavigationItem(mNews);
            } else if(getString(R.string.main_side_menu_logout).equals(tag)) {
                //highlightNavigationItem(mLogout);
            } else if(getString(R.string.main_side_menu_settings).equals(tag)) {
                highlightNavigationItem(mSettings);
            }
        }
    }
}
