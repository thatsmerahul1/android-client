package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ecarezone.android.patient.DoctorBioActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.adapter.DoctorsAdapter;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.model.Doctor;

import java.util.ArrayList;


/**
 * Created by CHAO WEI on 6/19/2015.
 */
public class SearchFragment extends EcareZoneBaseFragment {

    private static final String TAG = SearchFragment.class.getSimpleName();
    private ListView doctorListView = null;
    private DoctorsAdapter doctorsAdapter;
    private Bundle data;

    @Override
    protected String getCallerName() {
        return SearchFragment.class.getSimpleName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_search_user_list, container, false);

        data = getArguments();
        final ArrayList<Doctor> doctorList = data.getParcelableArrayList(Constants.DOCTOR_LIST);
        doctorListView = (ListView) view.findViewById(R.id.list_view_users);
        doctorsAdapter = new DoctorsAdapter(getActivity(), doctorList,false);
        doctorListView.setAdapter(doctorsAdapter);
        doctorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle data = new Bundle();
                data.putParcelable(Constants.DOCTOR_DETAIL, doctorList.get(position));
                final Activity activity = getActivity();

                if (activity != null) {
                    Intent showDoctorIntent = new Intent(activity.getApplicationContext(), DoctorBioActivity.class);
                    showDoctorIntent.putExtra(Constants.DOCTOR_BIO_DETAIL, data);
                    activity.startActivity(showDoctorIntent);
                }
            }
        });
        return view;
    }
}
