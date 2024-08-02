package com.example.mylib.ui.plan;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import com.example.mylib.api.plan.ERPlans;
import com.example.mylib.api.plan.ERPlansResponse;
import com.example.mylib.prefs.Datastore;

import java.util.ArrayList;

import okhttp3.ResponseBody;

public class PlansActivity extends AppCompatActivity {
    private static final String TAG = "PlansActivity";
    private Datastore mDatastore;
    private ProgressBar progressBar;

    private RecyclerView plansRecyclerView;
    private TextView txtMessage;
;

    public static void start(Context context) {
        Intent intent = new Intent(context, PlansActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);
        mDatastore = new Datastore(getApplicationContext());

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.activity_plans_progress);
        plansRecyclerView = findViewById(R.id.activity_plans_recycler_view);
        txtMessage = findViewById(R.id.activity_plans_message);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        plansRecyclerView.setLayoutManager(manager);
        progressBar.setVisibility(View.VISIBLE);

        getPlans();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPlans() {
        Location loc = KuvrrSDK.getCurrentLocation();
        if (loc != null) {
            UserLocationRequest requestContent = new UserLocationRequest(loc.getLatitude(), loc.getLongitude());
            ERPlans request = new ERPlans();
            request.getERPlans(requestContent, new ERPlans.OnResponseListener() {
                @Override
                public void onResponse(ArrayList<ERPlansResponse> result) {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "- onSuccess ");
                    if (result == null) {
                        return;
                    }
                    if (result.isEmpty()) {
                        txtMessage.setVisibility(View.VISIBLE);
                        txtMessage.setText(getString(R.string.erplans_error_message));
                        return;
                    }
                    plansRecyclerView.setAdapter(new PlansAdapter(PlansActivity.this,
                            result, onERPlanClickListener));
                }

                @Override
                public void onErrorResponse(ResponseBody errBody) {

                }

                @Override
                public void onFailure() {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    View.OnClickListener onERPlanClickListener = view -> {
        ERPlansResponse erPlan = (ERPlansResponse) view.getTag();
        PlanInfoActivity.start(PlansActivity.this, erPlan.uuid);
    };
}