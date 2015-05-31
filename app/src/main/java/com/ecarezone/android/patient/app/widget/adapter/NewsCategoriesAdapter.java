package com.ecarezone.android.patient.app.widget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecarezone.android.patient.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by CHAO WEI on 5/31/2015.
 */
public class NewsCategoriesAdapter extends BaseAdapter {

    public static String NEWS_CATEGORY_MAP_KEY_TITLE = "newsCategoryTitle";
    public static String NEWS_CATEGORY_MAP_KEY_IMAGE = "newsCategoryImage";

    private Context mContext = null;
    private ArrayList<HashMap<String, Integer>> mNewsCategories = null;

    public NewsCategoriesAdapter(Context context, ArrayList<HashMap<String, Integer>> categories) {
        mContext = context;
        mNewsCategories = categories;
    }

    @Override
    public int getCount() {
        return mNewsCategories.size();
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
        NewsCategoryItem nci = null;
        if (convertView == null) {
            Context context = ((parent.getContext() == null) ? mContext : parent.getContext());
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = (RelativeLayout) inflater.inflate(R.layout.template_news_categories_item, parent, false);
            nci = new NewsCategoryItem(convertView);
            convertView.setTag(nci);
        } else {
            nci = ((NewsCategoryItem) convertView.getTag());
        }

        if((nci != null) && (mNewsCategories != null)) {
            HashMap<String, Integer> item = mNewsCategories.get(position);
            nci.itemImage.setImageResource(item.get(NEWS_CATEGORY_MAP_KEY_IMAGE));
            nci.title.setText(item.get(NEWS_CATEGORY_MAP_KEY_TITLE));
        }

        return convertView;
    }

    static class NewsCategoryItem {
        final ImageView itemImage;
        final TextView title;

        NewsCategoryItem (final View view) {
            itemImage = (ImageView) view.findViewById(R.id.image_view_news_category_item);
            title = (TextView) view.findViewById(R.id.text_view_news_category_item);
        }
    }
}
