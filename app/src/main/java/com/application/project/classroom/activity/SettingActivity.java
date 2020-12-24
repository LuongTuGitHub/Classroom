package com.application.project.classroom.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.application.project.classroom.R;
import com.application.project.classroom.fragment.SettingFragment;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        FragmentManager manager = getFragmentManager();

        if (manager.findFragmentById(R.id.fm_setting)==null){
            manager.beginTransaction().add(R.id.fm_setting,new SettingFragment()).commit();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> finish());

        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);


    }

    private void routerSendFeedback(){
        Intent intent = new Intent(SettingActivity.this,SendFeedbackActivity.class);
        startActivity(intent);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar_vs,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.repos){
            routerSendFeedback();
        }
        return true;
    }
}