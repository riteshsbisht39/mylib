package com.example.mylib.ui.plan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylib.KuvrrSDK;
import com.example.mylib.R;
import com.example.mylib.api.plan.ERPInfo;
import com.example.mylib.api.plan.ERPInfoUpdate;
import com.example.mylib.api.plan.ERPlanInfoResponse;
import com.example.mylib.api.plan.ERPlanInfoVersion;
import com.example.mylib.api.plan.ERPlansInfoUpdateResponse;
import com.example.mylib.prefs.Datastore;

import okhttp3.ResponseBody;

public class PlanInfoActivity extends AppCompatActivity {

    private static final String TAG = "PlanInfoActivity";
    private Datastore mDatastore;
    public static final String INTENT_KEY_UUID = "com.safety.armourgrid.activity.ERPlanInfoActivity.INTENT_KEY_UUID";
    private TextView infoMesgText;
    private ProgressBar progressBar;
    private RecyclerView planDetailsRecyclerView;
    private Button sendAckButton;
    private String uuid;
    private ProgressDialog pDialog;
    private static Context context;


    public static void start(Context ctx, String puid) {
        context = ctx;
        Intent intent = new Intent(ctx, PlanInfoActivity.class);
        intent.putExtra(INTENT_KEY_UUID, puid);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_info);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(getString(R.string.activity_plans));

        mDatastore = new Datastore(getApplicationContext());
        uuid = getIntent().getStringExtra(INTENT_KEY_UUID);

        progressBar = findViewById(R.id.activity_plan_info_progress);
        planDetailsRecyclerView = findViewById(R.id.activity_plan_info_recycler_view);
        infoMesgText = findViewById(R.id.activity_plan_info_message);
        sendAckButton = findViewById(R.id.send_acknowledgement_button);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        planDetailsRecyclerView.setLayoutManager(manager);
        progressBar.setVisibility(View.VISIBLE);

        SpannableString infoText = new SpannableString(getString(R.string.erplan_info_message));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                PackageManager pm = getApplicationContext().getPackageManager();
                if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                    startActivityForCall( "911");
                } else {
                    KuvrrSDK.showToast("Phone calls not available on this device!");
                }
            }
        };

        int textLength = infoText.length();
        infoText.setSpan(clickableSpan,textLength-5,textLength-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        infoText.setSpan(new ForegroundColorSpan(Color.RED), textLength-5, textLength-1, 0);
        infoMesgText.setText(infoText);
        infoMesgText.setMovementMethod(LinkMovementMethod.getInstance());

        ERPInfo request = new ERPInfo();
        request.getERPInfo(uuid, new ERPInfo.OnResponseListener() {
            @Override
            public void onResponse(ERPlanInfoResponse result) {
                progressBar.setVisibility(View.GONE);
                Log.d( TAG,"ERPInfo - onSuccess ");
                if (result == null) {
                    return;
                }
                setDetails(result);
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

    private void setDetails(final ERPlanInfoResponse result) {
        pDialog = new ProgressDialog(PlanInfoActivity.this);
        pDialog.setMessage(getString(R.string.plan_ack_posting));
        pDialog.setCancelable(false);
        final int version = result.version;
        ActionBar bar = getSupportActionBar();
        bar.setTitle(result.title);
        planDetailsRecyclerView.setAdapter(new PlanInfoAdapter(result.acknowledgementDate, result.jsonData, new PlanInfoAdapter.InfoItemItemClickListener() {
            @Override
            public void onInfoItemClick(boolean enabled) {
                if (enabled) {
                    sendAckButton.setVisibility(View.VISIBLE);
                } else {
                    sendAckButton.setVisibility(View.GONE);
                }
            }
        }));

        sendAckButton.setOnClickListener(view -> {
            pDialog.show();
            ERPlanInfoVersion requestContent =  new ERPlanInfoVersion(version);
            ERPInfoUpdate request = new ERPInfoUpdate();
            request.updateERPInfo(uuid, requestContent, new ERPInfoUpdate.OnResponseListener() {
                @Override
                public void onResponse(ERPlansInfoUpdateResponse result) {
                    Log.d( TAG,"ERPInfoUpdate - onSuccess ");
                    pDialog.cancel();
                    if (result == null) {
                        return;
                    }
                    finish();
                    KuvrrSDK.showToast(getString(R.string.plan_ack_post_success));
                }

                @Override
                public void onErrorResponse(ResponseBody errBody) {
                    pDialog.cancel();
//                    longToast(parseErrorMessage(errBody));
                }

                @Override
                public void onFailure() {
                    pDialog.cancel();
                    KuvrrSDK.showToast(getString(R.string.simple_error));
                }
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void startActivityForCall(final String mobile) {
        try {
            if (mobile != null) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mobile));
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in calling" + e.getLocalizedMessage());
        }
    }
}