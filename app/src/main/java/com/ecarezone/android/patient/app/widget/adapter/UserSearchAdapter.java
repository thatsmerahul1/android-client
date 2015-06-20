package com.ecarezone.android.patient.app.widget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ecarezone.android.patient.R;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

/**
 * Created by CHAO WEI on 6/20/2015.
 */
public class UserSearchAdapter extends BaseAdapter {

    private Context mContext = null;
    private ArrayList<QBUser> mQbUsers = null;

    public UserSearchAdapter (Context context, ArrayList<QBUser> users) {
        mContext = context;
        mQbUsers = users;
    }
    @Override
    public int getCount() {
        return mQbUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.template_item_user, null);
            holder = new ViewHolder();
            holder.UserLoginName = (TextView) convertView.findViewById(R.id.text_view_user_login_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final QBUser user = mQbUsers.get(position);
        if (user != null) {
            holder.UserLoginName.setText(user.getLogin());
        }
        return convertView;
    }

    static class ViewHolder {
        TextView UserLoginName;
    }

}
