package uk.ac.hud.tjm3.helpme;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class Splash extends AppCompatActivity {
    final static int SPLASH_SCREEN_DELAY = 4000;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //WebView wv = (WebView) findViewById(R.id.loading_web_view);
        //wv.loadUrl("file://android_assets/loading.gif");

        this.setTitle("helpme");

        this.handler = new Handler();

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, Splash.SPLASH_SCREEN_DELAY);
    }
}
