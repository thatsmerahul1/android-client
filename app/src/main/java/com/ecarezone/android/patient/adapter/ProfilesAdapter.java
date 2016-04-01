package com.ecarezone.android.patient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.UserProfile;
import com.ecarezone.android.patient.model.database.ProfileDbApi;
import com.ecarezone.android.patient.utils.MD5Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by L&T Technology Services on 2/19/2016.
 */
public class ProfilesAdapter extends BaseAdapter {

    public static String PROFILE_MAP_KEY_TITLE = "profileTitle";
    public static String PROFILE_MAP_KEY_IMAGE = "profileImage";

    private Context mContext = null;
    private ArrayList<UserProfile> mProfiles = new ArrayList<UserProfile>();

    public ProfilesAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mProfiles.size();
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
        ProfileItem profileItem = null;

        if (convertView == null) {
            Context context = ((parent.getContext() == null) ? mContext : parent.getContext());
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_profile, parent, false);
            profileItem = new ProfileItem(convertView);
            convertView.setTag(profileItem);
        } else {
            profileItem = ((ProfileItem) convertView.getTag());
        }


        if ((profileItem != null) && (mProfiles != null)) {
            UserProfile item = mProfiles.get(position);

            // Set the profile name
            profileItem.title.setText(item.profileName);

            // if profile doesn't have avatarUrl, try to load Gravatar from his email.
            String imageUrl = null;
            if (item.avatarUrl == null || item.avatarUrl.equals("")) {
                //size of the profile picture to download from Gravatar. Giving the dimenstions of the image container(imageView)
                int imageSize = mContext.getResources().getDimensionPixelSize(R.dimen.profile_thumbnail_edge_size);
                if (item.email != null) {
                    // convert the email string to md5hex and pass it in the Gravatarl url.
                    String hashEmail = MD5Util.md5Hex(item.email);
                    imageUrl = Constants.GRAVATOR_URL + hashEmail + "?d=" + Constants.DEFAULT_GRAVATOR_IMAGE_URL + "?s=" + imageSize;
                } else {
                    imageUrl = null;
                }
            } else {
                imageUrl = item.avatarUrl;
            }

            // If imageUrl is available load it via Picasso
            if (imageUrl != null && imageUrl.trim().length() > 8) {
                Picasso.with(mContext)
                        .load(imageUrl)
                        .placeholder(R.drawable.news_other)
                        .error(R.drawable.news_other)
                        .into(profileItem.itemImage);
            } else {
                // if gravatar image or uploaded image is not available, load a local resource by default:
                profileItem.itemImage.setImageResource(R.drawable.news_other);
            }

            // To get the functionality of First item always "My profile" and last item always "Add people you care".
            LinearLayout profileLayout = (LinearLayout) convertView.findViewById(R.id.layout_profile);
            ((TextView) convertView.findViewById(R.id.textView)).setTextColor(mContext.getResources().getColorStateList(R.color.text_green_color_selector));

            if (mProfiles.size() == position + 1) {
                // This is last item in list, it should be "add new profile" and background should change.
                profileLayout.setBackgroundResource(R.drawable.blue_rectangle_layout_selector);
                ((TextView) convertView.findViewById(R.id.textView)).setTextColor(mContext.getResources().getColor(R.color.text_gray_color_selector));
            } else if (position == 0) {
                // This is first item in list. Should be "My profile". change UI accordingly.
                profileLayout.setBackgroundResource(R.drawable.green_rectangle_layout_selector);
            } else {
                // All the remaining elements in the middle.
                profileLayout.setBackgroundResource(R.drawable.blue_to_white_rectangle_layout_selector);
            }
        }
        return convertView;
    }

    // View holder for each list item
    static class ProfileItem {
        final ImageView itemImage;
        final TextView title;

        ProfileItem(final View view) {
            itemImage = (ImageView) view.findViewById(R.id.imageView);
            title = (TextView) view.findViewById(R.id.textView);
        }
    }

    public void setImageForParticularProfile(String profileId, String imagePath){

    }

    @Override
    public void notifyDataSetChanged() {
        // This is to refresh the data & UI after a profile is created/updated/deleted.
        ProfileDbApi profileDbApi = new ProfileDbApi(mContext);
        UserProfile[] tempProfiles = profileDbApi.getProfiles(LoginInfo.userId.toString());
        if (tempProfiles != null) {
            mProfiles = new ArrayList<UserProfile>(Arrays.asList(tempProfiles));
            // check whether the db contains more or single profile.
            // If no profiles, add "Create your profile" item to this mProfiles list
            if ((tempProfiles.length == 0) ||
                    ((tempProfiles.length == 1) && (!mProfiles.get(0).profileName
                            .equals(mContext.getResources().getString(R.string.profile_mine))))) {
                UserProfile myProfileItem = new UserProfile();
                myProfileItem.profileName = mContext.getResources().getString(R.string.profile_add_your_profile);
                mProfiles.add(myProfileItem);
            }
        }

        // Always adding, "Add people you care" item at the end of the list
        UserProfile addProfileField = new UserProfile();
        addProfileField.profileName = mContext.getResources().getString(R.string.profile_add_people_you_care);
        mProfiles.add(addProfileField);

        super.notifyDataSetChanged();
    }

    public ArrayList<UserProfile> getProfileList() {
        return mProfiles;
    }

}
