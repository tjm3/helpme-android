package uk.ac.hud.tjm3.helpme;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import uk.ac.hud.tjm3.helpme.http_api.UserSession;

public class LoginActivity extends AppCompatActivity {

    private UserSession userSession;
    public static final String TEST_LOGIN = "testaccount";
    public static final String TEST_PASSWORD = "testtest123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.userSession = UserSession.getInstance();

        // Sign in with test credentials, just for now
        this.userSession.signIn(TEST_LOGIN, TEST_PASSWORD);

        // TODO(tmkn8): Implement logging in later

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
}
