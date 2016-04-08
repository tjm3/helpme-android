package uk.ac.hud.tjm3.helpme;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.hud.tjm3.helpme.http_api.HelpRequestService;
import uk.ac.hud.tjm3.helpme.http_api.UserSession;

public class AddHelpRequestActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public final static int PLACE_PICKER_REQUEST = 231;
    public final static String TAG = "ADD_HREQUEST_ACTIVITY";
    private EditText titleEditText;
    private TextView datetimeTextView, errorTextView, contentTextView, locationNameTextView;
    private Button createButton, chooseDateButton, changeLocationButton;
    private HelpRequest newHelpRequest;
    private HelpRequestService service;
    private Handler handler = new Handler();
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_help_request);

        this.newHelpRequest = new HelpRequest();
        this.service = UserSession.getInstance().getService();

        this.titleEditText = (EditText) findViewById(R.id.title_edittext);
        this.createButton = (Button) findViewById(R.id.create_button);
        this.changeLocationButton = (Button) findViewById(R.id.change_location_button);
        this.locationNameTextView = (TextView) findViewById(R.id.location_name_textview);
        this.chooseDateButton = (Button) findViewById(R.id.choose_date_button);
        this.datetimeTextView = (TextView) findViewById(R.id.datetime_textview);
        this.errorTextView = (TextView) findViewById(R.id.error_textview);
        this.contentTextView = (TextView) findViewById(R.id.content_textview);

        this.chooseDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

                    // when dialog box is closed, below method will be called.
                    public void onDateSet(DatePicker view, final int selectedYear, final int selectedMonth, final int selectedDay) {
                        final Calendar calendar = Calendar.getInstance();


                        // Show time picker
                        TimePickerDialog timePicker = new TimePickerDialog(AddHelpRequestActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        Calendar newDate = Calendar.getInstance();
                                        newDate.set(selectedYear, selectedMonth, selectedDay, hourOfDay, minute);
                                        AddHelpRequestActivity.this.newHelpRequest.setMeetingDatetime(newDate.getTime());
                                        AddHelpRequestActivity.this.datetimeTextView.setText(newDate.getTime().toString());

                                        Log.d(TAG, "Set meeting date on new help request object: " + AddHelpRequestActivity.this.newHelpRequest.getMeetingDatetime().toString());
                                    }
                                }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
                        timePicker.setTitle("Select the date");
                        timePicker.setCancelable(false);
                        timePicker.show();
                    }
                };

                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                DatePickerDialog datePicker = new DatePickerDialog(AddHelpRequestActivity.this,
                        datePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setTitle("Select the date");
                datePicker.show();
                datePicker.setCancelable(false);
            }
        });

        this. mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        this.changeLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddHelpRequestActivity.this.openPlacePicker();
            }
        });

        this.createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddHelpRequestActivity.this.newHelpRequest.setTitle(AddHelpRequestActivity.this.titleEditText.getText().toString());
                AddHelpRequestActivity.this.newHelpRequest.setContent(AddHelpRequestActivity.this.contentTextView.getText().toString());

                Call<HelpRequest> call = AddHelpRequestActivity.this.service.postHelpRequest(AddHelpRequestActivity.this.newHelpRequest);
                call.enqueue(new Callback<HelpRequest>() {
                    @Override
                    public void onResponse(Call<HelpRequest> call, Response<HelpRequest> response) {
                        if(!response.isSuccess()) {
                            try {
                                AddHelpRequestActivity.this.errorTextView.setText(response.errorBody().string());
                                AddHelpRequestActivity.this.errorTextView.setVisibility(View.VISIBLE);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        // If created...
                        if(response.code() == 201) {
                            Log.d(TAG, "Help request successfuly posted");
                            AddHelpRequestActivity.this.newHelpRequest = response.body();
                            AddHelpRequestActivity.this.openNewHelpRequestActivity();
                        }
                    }

                    @Override
                    public void onFailure(Call<HelpRequest> call, Throwable t) {
                        t.printStackTrace();
                        throw new RuntimeException();
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if( mGoogleApiClient != null ) {
            mGoogleApiClient.connect();
            Log.d(TAG, "Connect GoogleApiClient");
        }
    }

    @Override
    protected void onStop() {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
            Log.d(TAG, "Disconnect GoogleApiClient");
        }
        super.onStop();
    }

    private void openNewHelpRequestActivity() {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AddHelpRequestActivity.this, HelpRequestActivity.class);
                intent.putExtra(HelpRequestListActivity.EXTRA_HELP_REQUEST_ID, String.valueOf(AddHelpRequestActivity.this.newHelpRequest.getId()));
                startActivity(intent);
                finish();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                AddHelpRequestActivity.this.locationNameTextView.setText(place.getName());
                AddHelpRequestActivity.this.newHelpRequest.setLocationName(place.getName().toString());

            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection to Google Services failed.");
    }

    private void openPlacePicker() {
        if( mGoogleApiClient == null || !mGoogleApiClient.isConnected() ) {
            return;
        }

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
}
