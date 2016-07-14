package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ecarezone.android.patient.MainActivity;
import com.ecarezone.android.patient.ProfileDetailsActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.adapter.ProfilesAdapter;
import com.ecarezone.android.patient.model.UserProfile;

import java.util.ArrayList;

/**
 * Created by CHAO WEI on 5/1/2015.
 */
public class UserProfileFragment extends EcareZoneBaseFragment implements
        AdapterView.OnItemClickListener {

    public static int VIEW_PROFILE_REQUEST_CODE = 1001;
    ArrayList<UserProfile> mProfiesList;
    private ListView listView;
    private ProfilesAdapter adapter;

    @Override
    protected String getCallerName() {
        return UserProfileFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.list_view, container, false);
        listView = (ListView) view.findViewById(R.id.listView);

        ((MainActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getText(R.string.profile_actionbar_title));

        adapter = new ProfilesAdapter(getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        mProfiesList = adapter.getProfileList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (mProfiesList.get(position).profileId == null) {
            startActivityForResult(new Intent(getApplicationContext(), ProfileDetailsActivity.class)
                    .putExtra(ProfileDetailsActivity.IS_NEW_PROFILE, true), VIEW_PROFILE_REQUEST_CODE);
        } else {
            String profileId = mProfiesList.get(position).profileId;
            startActivityForResult(new Intent(getApplicationContext(), ProfileDetailsActivity.class)
                    .putExtra(ProfileDetailsActivity.IS_NEW_PROFILE, false)
                    .putExtra(ProfileDetailsActivity.PROFILE_ID, profileId), VIEW_PROFILE_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIEW_PROFILE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                /* Successfully created/deleted the profile. Show list of profiles.*/

                if(data != null){
                    imagePath = data.getStringExtra("imagePath");
                    if(imagePath != null) {
                        mProfile = (UserProfile) data.getSerializableExtra("mProfile");
                    }
                }

                getActivity().getSupportFragmentManager().popBackStack();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.remove(this);

                transaction.replace(R.id.screen_container, new UserProfileFragment());
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                transaction.addToBackStack(UserProfileFragment.class.getSimpleName());
                transaction.commit();
            } else {
                Log.d(this.getClass().getName(), "No profile changes done:");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String imagePath;
    private UserProfile mProfile;
}

