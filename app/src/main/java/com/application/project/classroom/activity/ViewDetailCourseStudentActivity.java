package com.application.project.classroom.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.application.project.classroom.R;
import com.application.project.classroom.adapter.ViewWeekStudentAdapter;
import com.application.project.classroom.module.AddLesson;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.object.Course;
import com.application.project.classroom.object.Person;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewDetailCourseStudentActivity extends AppCompatActivity implements AddLesson {
    private Toolbar toolbar;
    private String UUID;
    private DatabaseReference refDb;
    private Course course;
    private ViewWeekStudentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail_course_student);

        Init();

        toolbar.setNavigationOnClickListener(v -> finish());
        loadCourse();
    }

    private void Init() {
        toolbar = findViewById(R.id.toolbar);
        UUID = getIntent().getStringExtra("key");
        RecyclerView rv_week = findViewById(R.id.rv_week);
        rv_week.setLayoutManager(new LinearLayoutManager(ViewDetailCourseStudentActivity.this,RecyclerView.VERTICAL,false));
        refDb = FirebaseDatabase.getInstance().getReference();
        adapter = new ViewWeekStudentAdapter();
        adapter.setContext(ViewDetailCourseStudentActivity.this);
        adapter.setAddLesson(this);
        rv_week.setAdapter(adapter);
    }

    private void loadCourse() {
        refDb.child(Const.COURSE).child(UUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    course = snapshot.getValue(Course.class);
                    if (course != null) {
                        refDb.child(Const.ACCOUNT).child(course.getTeacher().hashCode() + "").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    Person person = snapshot.getValue(Person.class);
                                    if (person != null) {
                                        toolbar.setTitle(course.getNameCourse());
                                        toolbar.setSubtitle(person.getNameUser());
                                        if (course.getWeeks() != null) {
                                            adapter.setWeeks(course.getWeeks());
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void addLesson(View view, int position) {
    }


    @Override
    public void viewLesson(View view, int week, int lesson) {
        Intent intent = new Intent(ViewDetailCourseStudentActivity.this, ViewLessonActivity.class);
        intent.putExtra("UUID", UUID);
        intent.putExtra("week", week);
        intent.putExtra("lesson", lesson);
        startActivity(intent);
    }
}