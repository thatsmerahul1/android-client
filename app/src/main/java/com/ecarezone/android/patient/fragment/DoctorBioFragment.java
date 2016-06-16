package com.ecarezone.android.patient.fragment;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.patient.DoctorBioActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.fragment.dialog.AddDoctorRequestDialog;
import com.ecarezone.android.patient.model.Doctor;
import com.ecarezone.android.patient.model.rest.AddDoctorRequest;
import com.ecarezone.android.patient.model.rest.AddDoctorResponse;
import com.ecarezone.android.patient.utils.ProgressDialogUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

/**
 * Created by L&T Technology Services on 22-02-2016.
 */
public class DoctorBioFragment extends EcareZoneBaseFragment {

    private static final String TAG = DoctorBioFragment.class.getSimpleName();
    private Bundle doctorBioData;
    private TextView doctorDescriptionView;
    private TextView doctorBioNameView;
    private TextView doctorBioCategoryView;
    private ImageView doctorBioImage;
    private ProgressDialog progressDialog;
    Doctor doctor;
    @Override
    protected String getCallerName() {
        return DoctorBioFragment.class.getSimpleName();
    }
    private static final int HTTP_STATUS_OK = 200;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.frag_doctor_bio, container, false);

        doctorBioData = getArguments();
        doctor = doctorBioData.getParcelable(Constants.DOCTOR_DETAIL);
        Log.i(TAG, "doctor in BIO = " + doctor);
        doctorDescriptionView = (TextView) view.findViewById(R.id.doctor_description);
        doctorBioNameView = (TextView) view.findViewById(R.id.doctor_bio_name_id);
        doctorBioCategoryView = (TextView) view.findViewById(R.id.doctor_bio_specialist_id);
        doctorBioImage = (ImageView) view.findViewById(R.id.doctor_bio_profile_pic_id);
        doctorBioNameView.setText("Dr. " + doctor.name);

        String imageUrl = doctor.avatarUrl;

        if (imageUrl != null && imageUrl.trim().length() > 8) {
            int dp = getActivity().getResources().getDimensionPixelSize(R.dimen.profile_thumbnail_edge_size);
            ;
            Picasso.with(getContext())
                    .load(imageUrl).resize(dp, dp)
                    .centerCrop().placeholder(R.drawable.news_other)
                    .error(R.drawable.news_other)
                    .into(doctorBioImage);
        }
        doctorBioCategoryView.setText(WordUtils.capitalize(doctor.doctorCategory));
        doctorDescriptionView.setText(doctor.doctorDescription);

        return view;
    }
  
}
