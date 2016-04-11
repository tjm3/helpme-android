package uk.ac.hud.tjm3.helpme;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import uk.ac.hud.tjm3.helpme.exceptions.InvalidLoginCredentialsRuntimeException;
import uk.ac.hud.tjm3.helpme.http_api.UserSession;

public class LoginActivity extends AppCompatActivity {

    private UserSession userSession;
    public static final String TEST_LOGIN = "testaccount";
    public static final String TEST_PASSWORD = "testtest123";
    private Button loginButton;
    private EditText loginEditText, passwordEditText;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.userSession = UserSession.getInstance();

        this.loginButton = (Button) findViewById(R.id.buttonLogin);
        this.loginEditText = (EditText) findViewById(R.id.txtNewUsername);
        this.passwordEditText = (EditText) findViewById(R.id.txtNewPassword);

        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.loginButton.setClickable(false);
                String password = LoginActivity.this.passwordEditText.getText().toString();
                String login = LoginActivity.this.loginEditText.getText().toString();

                try {
                    LoginActivity.this.userSession.signIn(login, password);
                } catch(InvalidLoginCredentialsRuntimeException e) {
                    e.printStackTrace();
                    LoginActivity.this.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LoginActivity.this.loginButton.setClickable(true);

                        }
                    }, 3000);
                    return;

                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, HelpRequestListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 1000);
            }
        });



    }
}
