package com.application.project.classroom.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.application.project.classroom.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartAppActivity extends AppCompatActivity {

    private final static int TIME_DELAY = 1500;

    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_app);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        new Handler().postDelayed(this::startApp, TIME_DELAY);
    }

    private void startApp() {
        if (fUser == null) {
            routerIntroductionScreenApp();
        } else {
            routerLogin();
        }
    }

    private void routerLogin() {
        Intent intent = new Intent(StartAppActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void routerIntroductionScreenApp() {
        Intent intent = new Intent(StartAppActivity.this, IntroductionScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}