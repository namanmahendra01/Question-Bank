package com.naman.questionbank;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import static java.lang.Thread.sleep;

public class splash extends AppCompatActivity {
    ImageView splashimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        splashimage= findViewById(R.id.imagesplash);

        Animation myanim= AnimationUtils.loadAnimation(this,R.anim.myanim);
        splashimage.startAnimation(myanim);
        Thread myThread=new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    sleep(2000);

                    Intent i=new Intent(splash.this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        myThread.start();
    }

}
