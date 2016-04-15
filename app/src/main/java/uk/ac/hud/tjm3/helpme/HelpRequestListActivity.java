package uk.ac.hud.tjm3.helpme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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

import java.security.Security;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.RunnableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.hud.tjm3.helpme.http_api.HelpRequestService;
import uk.ac.hud.tjm3.helpme.http_api.UserSession;

public class HelpRequestListActivity extends AppCompatActivity {
    public final static DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance();
    public final static String EXTRA_HELP_REQUEST_ID = "uk.ac.hud.tjm3.helpme.HELP_REQUEST_ID";
    final static String TAG = "HELP_RLIST_ACTIVITY";
    private ArrayList<String> helpRequestsList = new ArrayList<String>();
    private List<HelpRequest> rawHelpRequestList = new ArrayList<HelpRequest>();
    private ArrayAdapter<String> helpRequestListAdapter;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location currentLocation;
    public static long LOCATION_REFRESH_TIME = 5000;
    public static float LOCATION_REFRESH_DISTANCE = 50;
    private Date nextUpdate = new Date();
    private Button refreshButton;
    private Handler handler = new Handler();
    private HelpRequestService service;
    private String provider;
    final int MY_PERMISSIONS_REQUEST_GPS = 143;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "HelpRequestListActivity onCreate inititated");

        setContentView(R.layout.activity_help_request_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("helpme requests");

        DATE_FORMAT.setTimeZone(TimeZone.getDefault());

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


        // Set up GPS
        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        this.provider = LocationManager.GPS_PROVIDER;
        boolean enabled = this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        this.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Date now = new Date();
                if (location != null) {
                    Log.d(TAG, "Location changed: " + location.toString());
                    HelpRequestListActivity.this.currentLocation = location;

                    if(now.after(nextUpdate)) {
                        HelpRequestListActivity.this.refreshHelpRequests();
                        nextUpdate.setSeconds(nextUpdate.getSeconds() + 5);
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "Location provider enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "Location provider disabled");
            }
        };

        this.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(HelpRequestListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HelpRequestListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HelpRequestListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_GPS);
                }
                HelpRequestListActivity.this.currentLocation = HelpRequestListActivity.this.locationManager.getLastKnownLocation(HelpRequestListActivity.this.provider);
                HelpRequestListActivity.this.locationListener.onLocationChanged(HelpRequestListActivity.this.currentLocation);
                HelpRequestListActivity.this.locationManager.requestLocationUpdates(HelpRequestListActivity.this.provider, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, HelpRequestListActivity.this.locationListener);
            }
        }, 1000);


        Log.d(TAG, "onCreate on HelpRequestListActivity finished");
    }

    private void refreshHelpRequests() throws NullPointerException {
        Log.d(TAG, "Refresh help requests initiated");
        this.helpRequestsList.clear();
        this.rawHelpRequestList.clear();
        this.helpRequestListAdapter.notifyDataSetChanged();

        if(this.currentLocation == null) {
           return;
        }

        Call<List<HelpRequest>> call = this.service.getHelpRequestList(this.currentLocation.getLongitude(), this.currentLocation.getLatitude());
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
                        HelpRequestListActivity.this.helpRequestsList.add(helpRequest.getTitle() + "     " + helpRequest.getLocationName() + "\n" + DATE_FORMAT.format(helpRequest.getMeetingDatetime()) + "\nby @" + helpRequest.getAuthorName());
                    }
                } else {
                    HelpRequestListActivity.this.helpRequestsList.add("No help requests in the area :(");
                }

                HelpRequestListActivity.this.helpRequestListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<HelpRequest>> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }

}
