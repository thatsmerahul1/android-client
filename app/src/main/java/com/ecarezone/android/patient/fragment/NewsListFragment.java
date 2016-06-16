package com.ecarezone.android.patient.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ecarezone.android.patient.NewsListActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.adapter.NewsListAdapter;
import com.ecarezone.android.patient.fragment.dialog.NewsWebviewDialogFragment;
import com.ecarezone.android.patient.model.News;
import com.ecarezone.android.patient.view.SingleNewsItem;

import java.util.ArrayList;

/**
 * Created by L&T Technology Services on 2/25/2016.
 */
public class NewsListFragment extends EcareZoneBaseFragment implements AdapterView.OnItemClickListener {

    public static String NEWS_TITLE = "news_title";
    public static String NEWS_LINK = "news_link";
    public static String NEWS_DESCRIPTION = "description";

    public ArrayList<News> mNews;
    public static int REQUEST_SHOW_NEWS_IN_WEB_VIEW = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.list_view, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listView);

        Bundle bundle = getArguments();
        mNews = bundle.getParcelableArrayList(NewsCategoriesFragment.NEWS_PARCELABLE);
        String categoryName = bundle.getString(NewsCategoriesFragment.NEWS_CATEGORY_NAME);

        listView.setAdapter(new NewsListAdapter(getApplicationContext(), mNews));
        listView.setOnItemClickListener(this);

        ((NewsListActivity) getActivity()).getSupportActionBar()
                .setTitle(categoryName);
        return view;
    }

    @Override
    protected String getCallerName() {
        return NewsListFragment.class.getSimpleName();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        News news = mNews.get(position);

        Bundle bundle = new Bundle();
        bundle.putString(NEWS_TITLE, news.newsTitle);
        bundle.putString(NEWS_LINK, news.newsLink);
        bundle.putString(NEWS_DESCRIPTION, news.newsAbstract);


        Intent intent = new Intent(getActivity(), SingleNewsItem.class);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
//        NewsWebviewDialogFragment webviewDialogFragment = new NewsWebviewDialogFragment();
//        webviewDialogFragment.setTargetFragment(this, REQUEST_SHOW_NEWS_IN_WEB_VIEW);
//        webviewDialogFragment.setArguments(bundle);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        webviewDialogFragment.show(fragmentManager, "NewsDialogFragment");
    }
}