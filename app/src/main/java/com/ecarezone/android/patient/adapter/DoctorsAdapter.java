package com.ecarezone.android.patient.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.model.Doctor;
import com.ecarezone.android.patient.utils.ImageUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
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
        int dp = activity.getResources().getDimensionPixelSize(R.dimen.profile_thumbnail_edge_size);

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
        String imageUrl = doctor.avatarUrl;

        if (imageUrl != null && imageUrl.trim().length() > 8) {
            Picasso.with(activity)
                    .load(imageUrl).resize(dp, dp)
                    .centerCrop().placeholder(R.drawable.news_other)
                    .error(R.drawable.news_other)
                    .into(holder.avatar);
        }
        holder.doctorType.setText(doctor.doctorCategory);
        if(doctor.status != null) {
            if (doctor.status.equalsIgnoreCase("1")) {
                holder.doctorAvailability.setText(R.string.doctor_available);
            } else {
                holder.doctorAvailability.setText(R.string.doctor_busy);
            }
        }
        setDoctorPresence(holder, doctor.status);

        return view;
    }

    /* scales & sets the image thumbnail to the profile image button*/
    private void setPic(String imagePath, int width, int height, ImageView avatar) {
        Bitmap bitmap = ImageUtil.createScaledBitmap(imagePath, width, height);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        boolean doApplyMatrix = false;
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                doApplyMatrix = true;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                doApplyMatrix = true;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                doApplyMatrix = true;
                break;
            default:
                break;
        }
        int dp = activity.getResources().getDimensionPixelSize(R.dimen.profile_thumbnail_edge_size);

        if (doApplyMatrix) {
            Picasso.with(activity)
                    .load(imagePath).resize(dp, dp)
                    .centerCrop().placeholder(R.drawable.news_other)
                    .error(R.drawable.news_other)
                    .into(avatar);
//            Bitmap imgBitmap = Bitmap.createBitmap(bitmap, 0,
//                    0, bitmap.getWidth(), bitmap.getHeight(),
//                    matrix, true);
//            avatar.setImageBitmap(imgBitmap);
        } else {
            Picasso.with(activity)
                    .load(imagePath).resize(dp, dp)
                    .centerCrop().placeholder(R.drawable.news_other)
                    .error(R.drawable.news_other)
                    .into(avatar);
//            avatar.setImageBitmap(bitmap);
        }
    }


    private void setDoctorPresence(ViewHolder holder, String status) {
        if(status != null) {
            if (status.equalsIgnoreCase("available")) {
                holder.doctorPresence.setBackground(activity.getResources().getDrawable(R.drawable.circle_green));
            } else if (status.equalsIgnoreCase("busy")) {
                holder.doctorPresence.setBackground(activity.getResources().getDrawable(R.drawable.circle_red));
            }
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
