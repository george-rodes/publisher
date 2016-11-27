package br.com.anagnostou.publisher;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView textView = (TextView) findViewById(R.id.tvMove);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        textView.setAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
                finish();
            }
        }, 5000);
    }

    public void nextActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
