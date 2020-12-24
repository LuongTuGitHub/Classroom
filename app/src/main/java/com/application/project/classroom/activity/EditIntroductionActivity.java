package com.application.project.classroom.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.application.project.classroom.R;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.object.Course;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditIntroductionActivity extends AppCompatActivity {

    private Course course;
    private String UUID;

    private Toolbar toolbar;
    private Button bt_confirm_change;
    private EditText edt_introduction;


    private DatabaseReference refDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_introduction);

        UUID = getIntent().getStringExtra("key");
        if(UUID==null){
            finish();
        }

        Init();


        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> finish());

        bt_confirm_change.setOnClickListener(v -> changeIntroduction(edt_introduction.getText().toString()));

        loadCourse();
    }

    private void loadCourse() {
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

    private void Init(){
        refDb = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.toolbar);
        bt_confirm_change = findViewById(R.id.bt_confirm_change);
        edt_introduction = findViewById(R.id.edt_introduction);


    }

    private void changeIntroduction(String content){
        if((!edt_introduction.getText().toString().isEmpty())&&(!edt_introduction.getText().toString().equals(""))){
            course.setIntroductionCourse(content);
            refDb.child(Const.COURSE).child(UUID).setValue(course).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    edt_introduction.setText("");
                    Toast.makeText(EditIntroductionActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}