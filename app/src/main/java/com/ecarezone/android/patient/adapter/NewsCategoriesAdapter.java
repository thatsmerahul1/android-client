package com.ecarezone.android.patient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.model.NewsCategory;
import com.squareup.picasso.Picasso;

/**
 * Created by CHAO WEI on 5/31/2015.
 */
public class NewsCategoriesAdapter extends BaseAdapter {

    private Context mContext = null;
    private NewsCategory[] mNewsCategories = null;

    public NewsCategoriesAdapter(Context context, NewsCategory[] categories) {
        mContext = context;
        mNewsCategories = categories;
    }

    @Override
    public int getCount() {
        return mNewsCategories.length;
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
        NewsCategoryItem newsCategoryItem = null;

        if (convertView == null) {
            Context context = ((parent.getContext() == null) ? mContext : parent.getContext());
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.template_news_categories_item, parent, false);
            newsCategoryItem = new NewsCategoryItem(convertView);
            convertView.setTag(newsCategoryItem);
        } else {
            newsCategoryItem = ((NewsCategoryItem) convertView.getTag());
        }

        if ((newsCategoryItem != null) && (mNewsCategories != null)) {
            NewsCategory item = mNewsCategories[position];
            // sets each grid item with image and news title
            if (item.newsImageLink != null && item.newsImageLink.trim().length() > 8) {
                Picasso.with(mContext)
                        .load(item.newsImageLink)
                        .fit()
                        .placeholder(R.drawable.news_other)
                        .error(R.drawable.news_other)
                        .into(newsCategoryItem.itemImage);
            }
            newsCategoryItem.title.setText(item.newsCategory);
        }
        return convertView;
    }

    // View holder for each item in the gridview
    static class NewsCategoryItem {
        final ImageView itemImage;
        final TextView title;

        NewsCategoryItem(final View view) {
            itemImage = (ImageView) view.findViewById(R.id.image_view_news_category_item);
            title = (TextView) view.findViewById(R.id.text_view_news_category_item);
        }
    }
}
