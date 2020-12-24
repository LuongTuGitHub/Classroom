package com.application.project.classroom.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.application.project.classroom.R;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.object.Course;
import com.application.project.classroom.object.Week;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddWeekCourseActivity extends AppCompatActivity {
    private String UUID;
    private Course course;


    private DatabaseReference refDb;


    private Toolbar toolbar;
    private EditText edit_description;
    private Button bt_add_week;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_week_course);

        UUID = getIntent().getStringExtra("key");
        if(UUID==null){
            finish();
        }

        Init();

        setSupportActionBar(toolbar);


        toolbar.setNavigationOnClickListener(v -> finish());

        loadCourse();

        bt_add_week.setOnClickListener(v -> {
            if(edit_description.getText().toString().trim().length()>0){
                addWeek(edit_description.getText().toString());
            }
        });

    }

    private void loadCourse(){
        refDb.child(Const.COURSE).child(UUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    course = snapshot.getValue(Course.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addWeek(String description){
        List<Week> weeks = course.getWeeks();
        if(weeks==null){
            weeks = new ArrayList<>();
        }
        weeks.add(new Week(description));
        course.setWeeks(weeks);
        refDb.child(Const.COURSE).child(UUID).setValue(course).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                edit_description.setText("");
                Toast.makeText(AddWeekCourseActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Init(){
        refDb = FirebaseDatabase.getInstance().getReference();
        toolbar = findViewById(R.id.toolbar);

        edit_description = findViewById(R.id.edt_description);
        bt_add_week = findViewById(R.id.bt_add_week);

    }
}