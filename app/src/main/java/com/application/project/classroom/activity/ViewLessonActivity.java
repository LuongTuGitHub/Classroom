package com.application.project.classroom.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.application.project.classroom.R;
import com.application.project.classroom.adapter.PageCourseAdapter;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.object.Lesson;
import com.application.project.classroom.object.Page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewLessonActivity extends AppCompatActivity {

    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private Toolbar toolbar;
    private PageCourseAdapter adapter;
    private String UUID;
    private int week,lesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lesson);
        Init();
        if((UUID==null)||(week==-1)||(lesson==-1)){
            finish();
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        if(fUser==null){
            finish();
        }
        toolbar.setTitle("Week "+(week+1)+"/"+"Lesson "+(lesson+1));
        loadCourse();
    }

    private void Init(){
        UUID = getIntent().getStringExtra("UUID");
        week = getIntent().getIntExtra("week",-1);
        lesson = getIntent().getIntExtra("lesson",-1);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        toolbar = findViewById(R.id.toolbar);
        adapter = new PageCourseAdapter();
        RecyclerView rv_page = findViewById(R.id.rv_page);
        rv_page.setLayoutManager(new LinearLayoutManager(ViewLessonActivity.this,RecyclerView.VERTICAL,false));
        rv_page.setAdapter(adapter);

    }

    private void loadCourse(){
        refDb.child(Const.COURSE).child(UUID).child("weeks").child(week+"").child("lessons").child(lesson+"").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    Lesson lesson = snapshot.getValue(Lesson.class);
                    if(lesson!=null){
                        toolbar.setSubtitle(lesson.getTitle());
                        adapter.setPages(lesson.getPages());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}