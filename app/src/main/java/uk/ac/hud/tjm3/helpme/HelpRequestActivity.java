package uk.ac.hud.tjm3.helpme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private TextView datetimeTextView, contentTextView, authorTextView, locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_request);

        this.datetimeTextView = (TextView) findViewById(R.id.datetime_textview);
        this.contentTextView = (TextView) findViewById(R.id.content_textview);
        this.authorTextView = (TextView) findViewById(R.id.author_textview);
        this.locationTextView = (TextView) findViewById(R.id.location_textview);

        this.service = UserSession.getInstance().getService();

        Intent intent = getIntent();
        int helpRequestId = Integer.valueOf(intent.getStringExtra(HelpRequestListActivity.EXTRA_HELP_REQUEST_ID));
        Log.d(TAG, "Received ID in intent: " + helpRequestId);

        this.loadHelpRequest(helpRequestId);

    }

    private void loadHelpRequest(int id) {
        Call<HelpRequest> call = this.service.getHelpRequest(id);
        call.enqueue(new Callback<HelpRequest>() {
            @Override
            public void onResponse(Call<HelpRequest> call, Response<HelpRequest> response) {
                HelpRequestActivity.this.helpRequest = (HelpRequest) response.body();
                HelpRequestActivity.this.setTitle(HelpRequestActivity.this.helpRequest.getTitle());
                HelpRequestActivity.this.datetimeTextView.setText("Date: " + HelpRequestActivity.this.helpRequest.getDatetime().toString());
                HelpRequestActivity.this.contentTextView.setText(HelpRequestActivity.this.helpRequest.getContent());
                HelpRequestActivity.this.authorTextView.setText("Author: " + HelpRequestActivity.this.helpRequest.getAuthorName());
                HelpRequestActivity.this.locationTextView.setText("Location: " + HelpRequestActivity.this.helpRequest.getLocationName());
            }

            @Override
            public void onFailure(Call<HelpRequest> call, Throwable t) {

            }
        });

    }
}
