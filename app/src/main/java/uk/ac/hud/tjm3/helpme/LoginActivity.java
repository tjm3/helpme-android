package uk.ac.hud.tjm3.helpme;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.hud.tjm3.helpme.exceptions.InvalidLoginCredentialsRuntimeException;
import uk.ac.hud.tjm3.helpme.http_api.HelpRequestService;
import uk.ac.hud.tjm3.helpme.http_api.ServiceGenerator;
import uk.ac.hud.tjm3.helpme.http_api.UserSession;

public class LoginActivity extends AppCompatActivity {

    public final static String TAG = "LOGIN_ACTIVITY";
    private UserSession userSession;
    public static final String TEST_LOGIN = "testaccount";
    public static final String TEST_PASSWORD = "testtest123";
    private Button loginButton;
    private EditText loginEditText, passwordEditText;
    private TextView errorMessageTextView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.loginButton = (Button) findViewById(R.id.buttonLogin);
        this.loginEditText = (EditText) findViewById(R.id.txtNewUsername);
        this.passwordEditText = (EditText) findViewById(R.id.txtNewPassword);
        this.errorMessageTextView = (TextView) findViewById(R.id.error_message_text_view);

        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.loginButton.setClickable(false);
                String password = LoginActivity.this.passwordEditText.getText().toString();
                String login = LoginActivity.this.loginEditText.getText().toString();

                LoginActivity.this.hideErrorMessage();

                final HelpRequestService helpRequestService = ServiceGenerator.createService(HelpRequestService.class, login, password);

                Call<User> call = helpRequestService.getCurrentUser();
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) throws RuntimeException {
                        if (!response.isSuccess()) {
                            LoginActivity.this.setErrorMessage("Invalid login credentials. Try again.");

                            LoginActivity.this.handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    LoginActivity.this.loginButton.setClickable(true);

                                }
                            }, 3000);
                            return;
                        }

                        UserSession.getInstance().setService(helpRequestService);
                        UserSession.getInstance().setCurrentUser(response.body());
                        Log.d(
                                TAG,
                                "Logged in as: " + UserSession.getInstance().getCurrentUser().toString() +
                                " - " + UserSession.getInstance().getCurrentUser().getUsername() +
                                " - " + UserSession.getInstance().getCurrentUser().getEmail()
                        );

                        LoginActivity.this.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(LoginActivity.this, HelpRequestListActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        LoginActivity.this.setErrorMessage("No connection to the network or our server is not responding.");
                    }
                });
            }
        });
    }

    private void setErrorMessage(String errorMessage) {
        this.errorMessageTextView.setText(errorMessage);
        this.errorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage() {
        this.errorMessageTextView.setVisibility(View.GONE);
    }
}
