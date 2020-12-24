package com.application.project.classroom.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;

import com.application.project.classroom.R;
import com.google.android.material.snackbar.Snackbar;

public class IntroductionScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction_screen);

        if(checkNetWork()){
            FrameLayout lo_login = findViewById(R.id.lo_introduction);
            Snackbar snackbar =  Snackbar.make(lo_login,R.string.not_network, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }

        Button bt_start = findViewById(R.id.bt_start);

        bt_start.setOnClickListener(v -> routerLogin());

    }

    private boolean checkNetWork(){
        ConnectivityManager mConnect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnect.getActiveNetworkInfo();
        return (info == null) || (!info.isConnected());
    }

    private void routerLogin(){
        Intent intent = new Intent(IntroductionScreenActivity.this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(checkNetWork()){
            FrameLayout lo_login = findViewById(R.id.lo_introduction);
            Snackbar snackbar =  Snackbar.make(lo_login,R.string.not_network, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }
    }
}