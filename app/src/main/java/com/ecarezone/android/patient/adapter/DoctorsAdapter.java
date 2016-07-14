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
import com.ecarezone.android.patient.model.database.ChatDbApi;
import com.ecarezone.android.patient.utils.ImageUtil;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

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
    private ChatDbApi chatDbApi;
    private boolean reqPending;

    public DoctorsAdapter(Activity activity, ArrayList<Doctor> doctorList, boolean reqPending) {
        this.activity = activity;
        this.doctorList = doctorList;
        chatDbApi = ChatDbApi.getInstance(activity);
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.reqPending = reqPending;
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

        if (view == null) {
        holder = new ViewHolder();
            if(reqPending){
                view = inflater.inflate(R.layout.request_pending_item, null); //for request pending list
            } else {
                view = inflater.inflate(R.layout.doctor_list_item_layout, null); // my care and recommonded list
                holder.doctorPresence = view.findViewById(R.id.doctor_presence);
                holder.chatCount = (TextView) view.findViewById(R.id.chat_count);
            }

            holder.avatar = (ImageView) view.findViewById(R.id.doctor_avatar);
            holder.doctorAvailability = (TextView) view.findViewById(R.id.doctor_status);
            holder.doctorName = (TextView) view.findViewById(R.id.doctor_name);
            holder.doctorType = (TextView) view.findViewById(R.id.doctor_type);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Doctor doctor = doctorList.get(position);

        if(reqPending){
            holder.doctorName.setText("Dr. " + doctor.name);
            if(doctor.doctorCategory == null){
                holder.doctorType.setText(WordUtils.capitalize(doctor.category));
            }
            else {
                holder.doctorType.setText(WordUtils.capitalize(doctor.doctorCategory));
            }
            holder.doctorAvailability.setText("Pending request");
            String imageUrl = doctor.avatarUrl;

            if (imageUrl != null && imageUrl.trim().length() > 8) {
                Picasso.with(activity)
                        .load(imageUrl).resize(dp, dp)
                        .centerCrop().placeholder(R.drawable.news_other)
                        .error(R.drawable.news_other)
                        .into(holder.avatar);
            }
            notifyDataSetChanged();

        } else {
            holder.doctorName.setText("Dr. " + doctor.name);
            String imageUrl = doctor.avatarUrl;

            if (imageUrl != null && imageUrl.trim().length() > 8) {
                Picasso.with(activity)
                        .load(imageUrl).resize(dp, dp)
                        .centerCrop().placeholder(R.drawable.news_other)
                        .error(R.drawable.news_other)
                        .into(holder.avatar);
            }

            if (doctor.doctorCategory == null) {
                holder.doctorType.setText(WordUtils.capitalize(doctor.category));
            } else {
                holder.doctorType.setText(WordUtils.capitalize(doctor.doctorCategory));
            }
            if (doctor.emailId != null) {
                String count = String.valueOf(chatDbApi.getUnReadChatCountByUserId(doctor.emailId));
                if (!count.equalsIgnoreCase("0")) {
                    holder.chatCount.setText(count);
                    holder.chatCount.setVisibility(View.VISIBLE);
                } else {
                   holder.chatCount.setVisibility(View.GONE);
                }
            } else {
               holder.chatCount.setVisibility(View.GONE);
            }

            if (doctor.status != null) {
                if (doctor.status.equalsIgnoreCase("0")) {
                    holder.doctorAvailability.setText(R.string.doctor_busy);
                } else if (doctor.status.equalsIgnoreCase("1")) {
                    holder.doctorAvailability.setText(R.string.doctor_available);
                } else {
                    holder.doctorAvailability.setText(R.string.doctor_idle);
                }
            }
            setDoctorPresence(holder, doctor.status);
        }
        notifyDataSetChanged();
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
        } else {
            Picasso.with(activity)
                    .load(imagePath).resize(dp, dp)
                    .centerCrop().placeholder(R.drawable.news_other)
                    .error(R.drawable.news_other)
                    .into(avatar);
        }
    }

    private void setDoctorPresence(ViewHolder holder, String status) {
        if(status != null) {
            if (status.equalsIgnoreCase("1")) {
                holder.doctorPresence.setBackground(activity.getResources().getDrawable(R.drawable.circle_green));
            } else if (status.equalsIgnoreCase("0")) {
               holder.doctorPresence.setBackground(activity.getResources().getDrawable(R.drawable.circle_red));
            } else {
                holder.doctorPresence.setBackground(activity.getResources().getDrawable(R.drawable.circle_amber));
            }
        }
    }
   // View holder for each item in list
    class ViewHolder {
        ImageView avatar;
        TextView doctorName;
        TextView doctorType;
        TextView chatCount;
        View doctorPresence;
        TextView doctorAvailability;
    }
}
