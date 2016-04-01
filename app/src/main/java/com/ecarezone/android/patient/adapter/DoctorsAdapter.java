package com.ecarezone.android.patient.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.model.Doctor;

import java.util.ArrayList;

/**
 * Created by 293890 on 19-02-2016.
 */
public class DoctorsAdapter extends BaseAdapter {

    private final static String TAG = DoctorsAdapter.class.getSimpleName();
    private Activity activity;
    private ArrayList<Doctor> doctorList;
    private static LayoutInflater inflater;

    public DoctorsAdapter(Activity activity, ArrayList<Doctor> doctorList) {
        this.activity = activity;
        this.doctorList = doctorList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return doctorList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (convertView == null) {
            view = inflater.inflate(R.layout.doctor_list_item_layout, null);
            holder = new ViewHolder();
            holder.avatar = (ImageView) view.findViewById(R.id.doctor_avatar);
            holder.doctorAvailability = (TextView) view.findViewById(R.id.doctor_status);
            holder.doctorName = (TextView) view.findViewById(R.id.doctor_name);
            holder.doctorPresence = view.findViewById(R.id.doctor_presence);
            holder.doctorType = (TextView) view.findViewById(R.id.doctor_type);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Doctor doctor = doctorList.get(position);
        holder.doctorName.setText("Dr. " + doctor.name);
        holder.doctorType.setText(doctor.doctorCategory);
        if(doctor.status.equalsIgnoreCase("1")) {
            holder.doctorAvailability.setText(R.string.doctor_available);
        }
        else{
            holder.doctorAvailability.setText(R.string.doctor_busy);
        }
        setDoctorPresence(holder, doctor.status);

        return view;
    }

    private void setDoctorPresence(ViewHolder holder, String status) {
        if (status.equalsIgnoreCase("available")) {
            holder.doctorPresence.setBackground(activity.getResources().getDrawable(R.drawable.circle_green));
        } else if (status.equalsIgnoreCase("busy")) {
            holder.doctorPresence.setBackground(activity.getResources().getDrawable(R.drawable.circle_red));
        }
    }

    class ViewHolder {
        ImageView avatar;
        TextView doctorName;
        TextView doctorType;
        View doctorPresence;
        TextView doctorAvailability;
    }
}
