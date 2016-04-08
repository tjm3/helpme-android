package uk.ac.hud.tjm3.helpme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.hud.tjm3.helpme.http_api.HelpRequestService;
import uk.ac.hud.tjm3.helpme.http_api.UserSession;

public class HelpRequestActivity extends AppCompatActivity {

    final static String TAG = "HELP_REQUEST_ACTIVITY";
    private HelpRequestService service;
    private HelpRequest helpRequest;
    private TextView datetimeTextView, contentTextView, authorTextView, locationTextView, meetingDatetimeTextView, closeStatusTextView;
    private Button closeButton;
    private int helpRequestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_request);

        this.datetimeTextView = (TextView) findViewById(R.id.datetime_textview);
        this.contentTextView = (TextView) findViewById(R.id.content_textview);
        this.authorTextView = (TextView) findViewById(R.id.author_textview);
        this.locationTextView = (TextView) findViewById(R.id.location_textview);
        this.meetingDatetimeTextView = (TextView) findViewById(R.id.meeting_datetime_textview);
        this.closeStatusTextView = (TextView) findViewById(R.id.close_status_textview);
        this.closeButton = (Button) findViewById(R.id.close_button);

        this.service = UserSession.getInstance().getService();

        Intent intent = getIntent();
        this.helpRequestId = Integer.valueOf(intent.getStringExtra(HelpRequestListActivity.EXTRA_HELP_REQUEST_ID));
        Log.d(TAG, "Received ID in intent: " + this.helpRequestId);

        this.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpRequestActivity.this.helpRequest.setIsClosed(true);
                Call<HelpRequest> call = HelpRequestActivity.this.service.updateHelpRequest(HelpRequestActivity.this.helpRequest.getId(), HelpRequestActivity.this.helpRequest);
                call.enqueue(new Callback<HelpRequest>() {
                    @Override
                    public void onResponse(Call<HelpRequest> call, Response<HelpRequest> response) {
                        HelpRequestActivity.this.loadHelpRequest();
                    }

                    @Override
                    public void onFailure(Call<HelpRequest> call, Throwable t) {

                    }
                });

            }
        });
    }

    private void loadHelpRequest() {

        this.loadHelpRequest(this.helpRequestId);
    }

    private void loadHelpRequest(int id) {
        Call<HelpRequest> call = this.service.getHelpRequest(id);
        call.enqueue(new Callback<HelpRequest>() {
            @Override
            public void onResponse(Call<HelpRequest> call, Response<HelpRequest> response) {
                if (!response.isSuccess()) {
                    finish();
                    return;
                }

                HelpRequestActivity.this.helpRequest = (HelpRequest) response.body();
                HelpRequestActivity.this.setTitle(HelpRequestActivity.this.helpRequest.getTitle());
                HelpRequestActivity.this.datetimeTextView.setText("Posted: " + HelpRequestActivity.this.helpRequest.getDatetime().toString());
                HelpRequestActivity.this.meetingDatetimeTextView.setText("Meeting time: " + HelpRequestActivity.this.helpRequest.getMeetingDatetime().toString());
                HelpRequestActivity.this.contentTextView.setText(HelpRequestActivity.this.helpRequest.getContent());
                HelpRequestActivity.this.authorTextView.setText("Author: " + HelpRequestActivity.this.helpRequest.getAuthorName());
                HelpRequestActivity.this.locationTextView.setText("Location: " + HelpRequestActivity.this.helpRequest.getLocationName());

                if (HelpRequestActivity.this.isOwner() && !HelpRequestActivity.this.helpRequest.isClosed()) {
                    HelpRequestActivity.this.closeButton.setVisibility(View.VISIBLE);
                } else {
                    HelpRequestActivity.this.closeButton.setVisibility(View.GONE);
                }

                if (HelpRequestActivity.this.helpRequest.isClosed()) {
                    HelpRequestActivity.this.closeStatusTextView.setVisibility(View.VISIBLE);
                } else {
                    HelpRequestActivity.this.closeStatusTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<HelpRequest> call, Throwable t) {

            }
        });

    }

    private boolean isOwner() {
        if (this.helpRequest.getAuthorName().equals(UserSession.getInstance().getCurrentUser().getUsername())) {
            return true;
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.loadHelpRequest();
    }
}
