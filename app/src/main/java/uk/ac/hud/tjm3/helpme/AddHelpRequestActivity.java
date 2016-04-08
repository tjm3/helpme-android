package uk.ac.hud.tjm3.helpme;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.hud.tjm3.helpme.http_api.HelpRequestService;
import uk.ac.hud.tjm3.helpme.http_api.UserSession;

public class AddHelpRequestActivity extends AppCompatActivity {

    public final static String TAG = "ADD_HREQUEST_ACTIVITY";
    private EditText titleEditText, locationEditText;
    private TextView datetimeTextView, errorTextView, contentTextView;
    private Button createButton, chooseDateButton;
    private HelpRequest newHelpRequest;
    private HelpRequestService service;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_help_request);

        this.newHelpRequest = new HelpRequest();
        this.service = UserSession.getInstance().getService();

        this.titleEditText = (EditText) findViewById(R.id.title_edittext);
        this.locationEditText = (EditText) findViewById(R.id.location_edittext);
        this.createButton = (Button) findViewById(R.id.create_button);
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

        this.createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddHelpRequestActivity.this.newHelpRequest.setTitle(AddHelpRequestActivity.this.titleEditText.getText().toString());
                AddHelpRequestActivity.this.newHelpRequest.setLocationName(AddHelpRequestActivity.this.locationEditText.getText().toString());
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
                            AddHelpRequestActivity.this.openMainActivity();
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

    private void openMainActivity() {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AddHelpRequestActivity.this, HelpRequestListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
