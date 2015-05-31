package com.ecarezone.android.patient.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.app.widget.adapter.NewsCategoriesAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by CHAO WEI on 5/25/2015.
 */
public class NewsCategoriesFragment extends EcareZoneBaseFragment implements GridView.OnItemClickListener {

    private NewsCategoriesAdapter mNewsCategoriesAdapter = null;
    ArrayList<HashMap<String, Integer>> mNewsCategories = null;

    private int[] mImageResIdArray = {
            R.drawable.news_fitness, R.drawable.new_health_men,
            R.drawable.new_health_women, R.drawable.news_eating,
            R.drawable.news_family, R.drawable.news_other
    };

    private int[] mStringResIdArray = {
            R.string.news_categories_fitness, R.string.news_categories_men,
            R.string.news_categories_women, R.string.news_categories_eating,
            R.string.news_categories_family, R.string.news_categories_other
    };

    @Override
    protected String getCallerName() {
        return NewsCategoriesFragment.class.getSimpleName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewsCategories = new ArrayList<HashMap<String, Integer>>();
        for (int i = 0; i<mImageResIdArray.length; i++ ) {
            HashMap<String, Integer> data = new HashMap<String, Integer>();
            data.put(NewsCategoriesAdapter.NEWS_CATEGORY_MAP_KEY_TITLE, mStringResIdArray[i]);
            data.put(NewsCategoriesAdapter.NEWS_CATEGORY_MAP_KEY_IMAGE, mImageResIdArray[i]);
            mNewsCategories.add(data);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_news_categories, container, false);
        final GridView gv = (GridView) view.findViewById(R.id.grid_view_categories_list);
        mNewsCategoriesAdapter = new NewsCategoriesAdapter(getApplicationContext(), mNewsCategories);
        gv.setAdapter(mNewsCategoriesAdapter);
        gv.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
