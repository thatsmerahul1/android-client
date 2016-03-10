package com.ecarezone.android.patient.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.fragment.NewsListFragment;

/**
 * Created by L&T Technology Services on 2/26/2016.
 */
public class NewsWebviewDialogFragment extends DialogFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // retrieve the news info from the bundle
        Bundle bundle = getArguments();
        // title is for future use
        String newsTitle = bundle.getString(NewsListFragment.NEWS_TITLE);
        String url = bundle.getString(NewsListFragment.NEWS_LINK);

        View view = inflater.inflate(R.layout.dialog_frag_webview, container, false);

        WebView webView = (WebView) view.findViewById(R.id.webView);
        webView.loadUrl(url);

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        // Future: If buttons need to be added inside this layout.
    }
}