package com.application.project.classroom.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.application.project.classroom.R;

public class AddLessonCourseActivity extends AppCompatActivity {

    private String UUID;
    private int position;

    private Toolbar toolbar;

    private TextView tv_week;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lesson_course);

        setSupportActionBar(toolbar);
        Init();
        UUID = getIntent().getStringExtra("key");
        position = getIntent().getIntExtra("position",-1);
        if(UUID==null||(position==-1)){
            finish();
        }
        tv_week.setText("Week "+(position+1));

        toolbar.setNavigationOnClickListener(v -> finish());


    }

    private void Init(){
        toolbar = findViewById(R.id.toolbar);
        tv_week = findViewById(R.id.tv_week);

    }
}