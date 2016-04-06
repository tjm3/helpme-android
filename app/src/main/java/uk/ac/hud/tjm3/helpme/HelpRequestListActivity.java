package uk.ac.hud.tjm3.helpme;

import android.Manifest;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import uk.ac.hud.tjm3.helpme.http_api.UserSession;

public class HelpRequestListActivity extends AppCompatActivity {

    final static String TAG = "HELP_RLIST_ACTIVITY";
    private ArrayList<String> helpRequestsList = new ArrayList<String>();
    private ArrayAdapter<String> helpRequestListAdapter;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public static long LOCATION_REFRESH_TIME = 20000;
    public static float LOCATION_REFRESH_DISTANCE = 50;
    private Button refreshButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "HelpRequestListActivity onCreate inititated");
        setContentView(R.layout.activity_help_request_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.refreshButton = (Button) findViewById(R.id.refresh_button);

        this.refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpRequestListActivity.this.refreshHelpRequests();
            }
        });

        // Set up a list view for the help requests
        this.helpRequestListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, helpRequestsList);


        // Button to add new help request
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // TODO(tmkn8): open create view
            }
        });

        ListView helpRequestsListView = (ListView) findViewById(R.id.help_requests_list_view);
        helpRequestsListView.setAdapter(this.helpRequestListAdapter);

        this.refreshHelpRequests();
        Log.d(TAG, "onCreate on HelpRequestListActivity finished");
    }

    private void refreshHelpRequests() {
        Log.d(TAG, "Refresh help requests initiated");
        final HelpRequestList hrl = new HelpRequestList(UserSession.getInstance().getService());
        hrl.reloadData();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                HelpRequestListActivity.this.helpRequestsList.clear();

                for (HelpRequest helpRequest : hrl.getHelpRequests()) {
                    Log.d(TAG, helpRequest.getTitle() + helpRequest.toString());
                    HelpRequestListActivity.this.helpRequestsList.add(helpRequest.getTitle() + helpRequest.toString());
                }

                HelpRequestListActivity.this.helpRequestListAdapter.notifyDataSetChanged();
            }
        }, 1000);


    }

}
