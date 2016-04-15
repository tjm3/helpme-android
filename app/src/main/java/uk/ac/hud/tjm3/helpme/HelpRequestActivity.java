package uk.ac.hud.tjm3.helpme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private Button closeButton, replyButton, refreshButton;
    private EditText replyEditText;
    private ListView repliesListView;
    private ArrayAdapter<String> repliesArrayAdapter;
    private List<String> repliesList;
    private Handler handler = new Handler();
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
        this.repliesListView = (ListView) findViewById(R.id.help_requests_replies_list_view);
        this.replyButton = (Button) findViewById(R.id.send_reply_button);
        this.replyEditText = (EditText) findViewById(R.id.reply_edit_text);
        this.refreshButton = (Button) findViewById(R.id.refresh_button);

        this.repliesList = new ArrayList<String>();
        this.repliesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.repliesList);
        this.repliesListView.setAdapter(this.repliesArrayAdapter);

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

        this.replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpRequestActivity.this.replyButton.setClickable(false);
                HelpRequestReply reply = new HelpRequestReply();

                reply.setContent(HelpRequestActivity.this.replyEditText.getText().toString());
                HelpRequestActivity.this.replyEditText.setText("");
                reply.setHelpRequestId(HelpRequestActivity.this.helpRequest.getId());

                Call<HelpRequestReply> call = HelpRequestActivity.this.service.sendHelpRequestReply(reply);
                call.enqueue(new Callback<HelpRequestReply>() {
                    @Override
                    public void onResponse(Call<HelpRequestReply> call, Response<HelpRequestReply> response) {
                        if(!response.isSuccess()) {
                            Log.d(TAG, "replying to help request wasn't succesful");

                        } else {
                            HelpRequestActivity.this.loadHelpRequest();
                        }

                        HelpRequestActivity.this.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                HelpRequestActivity.this.replyButton.setClickable(true);
                            }
                        }, 2000);
                    }

                    @Override
                    public void onFailure(Call<HelpRequestReply> call, Throwable t) {
                        t.printStackTrace();
                    }
                });

            }
        });

        this.refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpRequestActivity.this.refreshButton.setClickable(false);
                HelpRequestActivity.this.loadHelpRequest();
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
                long now = System.currentTimeMillis();
                CharSequence posted = DateUtils.getRelativeTimeSpanString(HelpRequestActivity.this.helpRequest.getDatetime().getTime(), now, DateUtils.MINUTE_IN_MILLIS);
                HelpRequestActivity.this.datetimeTextView.setText("Posted: " + posted.toString());
                HelpRequestActivity.this.meetingDatetimeTextView.setText("Meeting time: " + HelpRequestListActivity.DATE_FORMAT.format(HelpRequestActivity.this.helpRequest.getMeetingDatetime()));
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

                HelpRequestActivity.this.loadHelpRequestReplies();

                HelpRequestActivity.this.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        HelpRequestActivity.this.refreshButton.setClickable(true);
                    }
                }, 3000);
            }

            @Override
            public void onFailure(Call<HelpRequest> call, Throwable t) {

            }
        });

    }

    private void loadHelpRequestReplies() {
        Log.d(TAG, "Load help request replies");

        this.repliesList.clear();

        List<HelpRequestReply> replies = this.helpRequest.getHelpRequestReplies();

        if(replies.size() > 0) {
            Log.d(TAG, "There're replies");
            for (HelpRequestReply reply : replies) {
                Log.d(TAG, "Reply: " + reply.getContent());
                this.repliesList.add(reply.getAuthorName() + "\n" + reply.getContent() + "\n" + HelpRequestListActivity.DATE_FORMAT.format(reply.getDatetime()).toString());
            }
        } else {
            Log.d(TAG, "There's no replies");
            this.repliesList.add("No replies to this help request.");
        }

        this.repliesArrayAdapter.notifyDataSetChanged();
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
