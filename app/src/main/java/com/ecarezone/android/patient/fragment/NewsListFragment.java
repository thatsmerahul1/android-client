package com.ecarezone.android.patient.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ecarezone.android.patient.NetworkCheck;
import com.ecarezone.android.patient.NewsListActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.adapter.NewsCategoriesAdapter;
import com.ecarezone.android.patient.adapter.NewsListAdapter;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.model.News;
import com.ecarezone.android.patient.model.NewsCategory;
import com.ecarezone.android.patient.model.rest.GetNewsRequest;
import com.ecarezone.android.patient.model.rest.GetNewsResponse;
import com.ecarezone.android.patient.utils.ProgressDialogUtil;
import com.ecarezone.android.patient.view.SingleNewsItem;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by L&T Technology Services on 2/25/2016.
 */
public class NewsListFragment extends EcareZoneBaseFragment implements AdapterView.OnItemClickListener {

    public static String NEWS_TITLE = "news_title";
    public static String NEWS_LINK = "news_link";
    public static String NEWS_DESCRIPTION = "description";

    private String categoryName;
    private ProgressDialog mProgressbar;

    private ArrayList<News> mNews;
    private ListView listView;
    public static int REQUEST_SHOW_NEWS_IN_WEB_VIEW = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.list_view, container, false);
        listView = (ListView) view.findViewById(R.id.listView);

        Bundle bundle = getArguments();

        if(bundle != null) {
            categoryName = bundle.getString(NewsCategoriesFragment.NEWS_CATEGORY_NAME);
            mNews = bundle.getParcelableArrayList(NewsCategoriesFragment.NEWS_PARCELABLE);
            if(mNews == null){
                retrieveNewItems();
            }
        }
        else{
            retrieveNewItems();
        }

        retrieveNewItems();
        initList();

        ((NewsListActivity) getActivity()).getSupportActionBar()
                .setTitle(categoryName);
        return view;
    }

    private void retrieveNewItems() {

        if(NetworkCheck.isNetworkAvailable(getActivity())) {
            if(mProgressbar != null && mProgressbar.isShowing()){
                mProgressbar.dismiss();
                mProgressbar = null;
            }
            mProgressbar = ProgressDialogUtil.getProgressDialog(getActivity(), getString(R.string.processing));
            mProgressbar.show();
            GetNewsRequest getNewsRequest = new GetNewsRequest();
            getSpiceManager().execute(getNewsRequest, "news", DurationInMillis.ONE_MINUTE, new NewsRequestListener());
        } else {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
        }

    }

    private void initList() {

        if(mNews != null) {
            listView.setAdapter(new NewsListAdapter(getApplicationContext(), mNews));
            listView.setOnItemClickListener(this);


            SharedPreferences sharedPreferences =
                    getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

            Set<String> categorySet = sharedPreferences.getStringSet(
                    Constants.NEWS_MESSAGE_CATEGORY_SET_KEY, null);

            if (categorySet != null) {
                Iterator<String> iterator = categorySet.iterator();
                while (iterator.hasNext()) {

                    String category = iterator.next();
                    if (categoryName.equalsIgnoreCase(category)) {
                        sharedPreferences.edit().putInt(Constants.NEWS_CATEGORY_PREPEND_STRING + categoryName, 0).apply();
                    }
                }
            }
        }
        else{
            // news not loaded...
        }
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
    }

    private class NewsRequestListener implements RequestListener<GetNewsResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            spiceException.printStackTrace();
        }

        @Override
        public void onRequestSuccess(GetNewsResponse response) {

            if(mProgressbar != null && mProgressbar.isShowing()){
                mProgressbar.dismiss();
            }

            GetNewsResponse mGetNewsResonse = response;
            NewsCategoriesAdapter mNewsCategoriesAdapter
                    = new NewsCategoriesAdapter(getApplicationContext(), mGetNewsResonse.data);

            NewsCategory[] newsList =  mGetNewsResonse.data;
            int position = 0;
            for(NewsCategory newsCategory : newsList) {
                ArrayList<News> newsArr = (ArrayList<News>) newsCategory.newsAbstractList;

                if (newsCategory.newsCategory.equalsIgnoreCase(categoryName)) {
                    // initialize relevant news list
                    mNews = newsArr;
                    break;
                }
            }
            initList();
        }
    }
}