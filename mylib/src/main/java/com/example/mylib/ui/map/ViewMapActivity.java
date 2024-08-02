package com.example.mylib.ui.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mylib.R;
import com.example.mylib.api.map.MapResponse;

public class ViewMapActivity extends AppCompatActivity {
    private static final String TAG = "ViewMapActivity";

    private static final String MAP_INFO = "map_info";
    private WebView webview;

    public static void start(Context context, MapResponse mapInfo) {
        Intent intent = new Intent(context, ViewMapActivity.class);
        intent.putExtra(MAP_INFO,mapInfo);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);

        Intent i = getIntent();
        MapResponse mapInfo = (MapResponse) i.getSerializableExtra(MAP_INFO);
        String mapUrl = mapInfo.getMapUrl();

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(mapInfo.getName());

        webview = findViewById(R.id.web_view_map);

        viewMap(mapUrl);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void viewMap(String url) {
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient());

        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        webview.loadUrl(url);
    }
}