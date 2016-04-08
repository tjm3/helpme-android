package uk.ac.hud.tjm3.helpme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RunnableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.hud.tjm3.helpme.http_api.HelpRequestService;
import uk.ac.hud.tjm3.helpme.http_api.UserSession;

public class HelpRequestListActivity extends AppCompatActivity {
    public final static String EXTRA_HELP_REQUEST_ID = "uk.ac.hud.tjm3.helpme.HELP_REQUEST_ID";
    final static String TAG = "HELP_RLIST_ACTIVITY";
    private ArrayList<String> helpRequestsList = new ArrayList<String>();
    private List<HelpRequest> rawHelpRequestList = new ArrayList<HelpRequest>();
    private ArrayAdapter<String> helpRequestListAdapter;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public static long LOCATION_REFRESH_TIME = 20000;
    public static float LOCATION_REFRESH_DISTANCE = 50;
    private Button refreshButton;
    private Handler handler = new Handler();
    private HelpRequestService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "HelpRequestListActivity onCreate inititated");
        setContentView(R.layout.activity_help_request_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("helpme requests");

        // Basic attributes
        this.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                HelpRequestListActivity.this.service = UserSession.getInstance().getService();
            }
        }, 1000);

        // Refresh buton
        this.refreshButton = (Button) findViewById(R.id.refresh_button);
        this.refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpRequestListActivity.this.refreshButton.setClickable(false);
                HelpRequestListActivity.this.refreshHelpRequests();
                HelpRequestListActivity.this.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        HelpRequestListActivity.this.refreshButton.setClickable(true);
                    }
                }, 5000);
            }
        });

        // Set up a list view for the help requests
        this.helpRequestListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.helpRequestsList);


        // Button to add new help request
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                HelpRequestListActivity.this.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(HelpRequestListActivity.this, AddHelpRequestActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        // Set up the list view
        final ListView helpRequestsListView = (ListView) findViewById(R.id.help_requests_list_view);
        helpRequestsListView.setAdapter(this.helpRequestListAdapter);
        helpRequestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HelpRequest helpRequest = HelpRequestListActivity.this.rawHelpRequestList.get(position);
                String helpRequestId = String.valueOf(helpRequest.getId());
                Log.d(TAG, "Sent ID in intent: " + helpRequestId);

                Intent intent = new Intent(HelpRequestListActivity.this, HelpRequestActivity.class);
                intent.putExtra(EXTRA_HELP_REQUEST_ID, helpRequestId);
                startActivity(intent);
                Log.d(TAG, "Clicked help request :" + helpRequest.toString());
            }
        });
        Log.d(TAG, "onCreate on HelpRequestListActivity finished");
    }

    private void refreshHelpRequests() {
        Log.d(TAG, "Refresh help requests initiated");
        this.helpRequestsList.clear();
        this.rawHelpRequestList.clear();
        this.helpRequestListAdapter.notifyDataSetChanged();


        Call<List<HelpRequest>> call = this.service.getHelpRequestList(5, 5);
        call.enqueue(new Callback<List<HelpRequest>>() {
            @Override
            public void onResponse(Call<List<HelpRequest>> call, Response<List<HelpRequest>> response) {
                if (!response.isSuccess()) {
                    HelpRequestListActivity.this.refreshHelpRequests();
                    return;
                }

                HelpRequestListActivity.this.rawHelpRequestList = response.body();

                if (rawHelpRequestList.size() > 0) {
                    for (HelpRequest helpRequest : rawHelpRequestList) {
                        HelpRequestListActivity.this.helpRequestsList.add(helpRequest.toString());
                    }
                } else {
                    HelpRequestListActivity.this.helpRequestsList.add("No help requests in the area :(");
                }

                HelpRequestListActivity.this.helpRequestListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<HelpRequest>> call, Throwable t) {
                t.printStackTrace();
                HelpRequestListActivity.this.refreshHelpRequests();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                HelpRequestListActivity.this.refreshHelpRequests();
            }
        }, 1200);

    }

}
