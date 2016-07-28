package com.ecarezone.android.patient;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.utils.Util;

/**
 * Created by L&T Technology Services on 2/29/2016.
 */
public class AboutEcareZoneActivity extends EcareZoneBaseActivity {
    private String ABOUT_ECAREZONE_URL = "http://ecarezone.com/#our_mission";

    @Override
    protected String getCallerName() {
        return null;
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_about_ecare);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        WebView webView=(WebView)findViewById(R.id.webview_about_ecare);
        webView.loadUrl(ABOUT_ECAREZONE_URL);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {

                progressBar.setVisibility(View.GONE);
            }

        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        Util.changeStatus(Constants.ONLINE,this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.changeStatus(Constants.IDLE, this);
    }
}
