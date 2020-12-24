package com.application.project.classroom.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

import com.application.project.classroom.R;

public class SendFeedbackActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);


        Init();

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void Init(){
        toolbar = findViewById(R.id.toolbar);
    }
}