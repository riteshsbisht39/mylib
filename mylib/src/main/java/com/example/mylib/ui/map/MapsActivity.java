package com.example.mylib.ui.map;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylib.KuvrrSDK;
import com.example.mylib.R;
import com.example.mylib.api.UserLocationRequest;
import com.example.mylib.api.map.MapResponse;
import com.example.mylib.api.map.Maps;
import com.example.mylib.api.map.MapsListResponse;
import com.example.mylib.prefs.Datastore;

import java.util.List;

import okhttp3.ResponseBody;

public class MapsActivity extends AppCompatActivity {

    private static final String TAG = "MapsActivity";
    private List<MapResponse> maps;

    private ProgressBar pBar;
    private TextView noMapText;
    RecyclerView mapRecyclerView;

    private String mapName;
    private String mapId;
    private Integer mapZoom;
    private Datastore mDatastore;
    private String userUuid, userName;


    public static void start(Context context) {
        Intent intent = new Intent(context, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mDatastore = new Datastore(getApplicationContext());

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(getString(R.string.activity_maps));

        pBar = findViewById(R.id.activity_map_progress);
        mapRecyclerView = findViewById(R.id.recycler_view_maps);
        noMapText = findViewById(R.id.text_no_map);

        loadMaps();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMaps() {
        Location loc = KuvrrSDK.getCurrentLocation();
        if (loc != null) {
            UserLocationRequest requestContent = new UserLocationRequest(loc.getLatitude(), loc.getLongitude());
            Maps request = new Maps();
            request.getMaps(requestContent, new Maps.OnResponseListener() {
                @Override
                public void onResponse(MapsListResponse result) {
                    Log.d(TAG,"Maps - onSuccess ");
                    if (result == null) {
                        return;
                    }
                    pBar.setVisibility(View.GONE);
                    maps = result.maps;
                    if (maps.isEmpty()) {
                        noMapText.setVisibility(View.VISIBLE);
                    } else {
                        noMapText.setVisibility(View.GONE);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(MapsActivity.this);
                        layoutManager.setOrientation(RecyclerView.VERTICAL);
                        mapRecyclerView.setLayoutManager(layoutManager);
                        mapRecyclerView.setVisibility(View.VISIBLE);
                        mapRecyclerView.setAdapter(new MapsAdapter(result.maps, (MapResponse map) -> {
                            if (map.getMapType().equalsIgnoreCase("Pdf")) {
//                                Intent pdfIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(map.getMapUrl()));
                                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                                pdfIntent.setDataAndType(Uri.parse(map.getMapUrl()), "application/pdf");
                                startActivity(pdfIntent);
                            } else if (map.getMapType().equalsIgnoreCase("Image")) {
                                ViewMapActivity.start(MapsActivity.this, map);
                            }
                        }));
                    }
                }

                @Override
                public void onErrorResponse(ResponseBody errBody) {

                }

                @Override
                public void onFailure() {
                    KuvrrSDK.showToast(getString(R.string.simple_error));
                }
            });
        }
    }
}