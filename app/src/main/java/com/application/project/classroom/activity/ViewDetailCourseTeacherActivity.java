package com.application.project.classroom.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.project.classroom.R;
import com.application.project.classroom.adapter.WeekTeacherAdapter;
import com.application.project.classroom.module.AddLesson;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.object.Course;
import com.application.project.classroom.object.Person;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewDetailCourseTeacherActivity extends AppCompatActivity implements AddLesson {
    private String UUID;

    private Toolbar toolbar;
    private TextView tv_class_code, tv_introduction;
    private Button bt_edit_introduction, bt_add_week;

    private DatabaseReference refDb;

    private Course course;
    private WeekTeacherAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail_course_teacher);
        UUID = getIntent().getStringExtra("key");
        if (UUID == null) {
            finish();
        }
        Init();

        setSupportActionBar(toolbar);


        toolbar.setNavigationOnClickListener(v -> finish());

        bt_edit_introduction.setOnClickListener(v -> routerEditIntroduction());
        tv_introduction.setOnClickListener(v -> routerEditIntroduction());

        bt_add_week.setOnClickListener(v -> routerAddWeek());
        loadCourse();
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
                                        tv_class_code.setText(course.getCourseID());
                                        tv_introduction.setText(course.getIntroductionCourse());
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

    private void routerEditIntroduction() {
        Intent intent = new Intent(ViewDetailCourseTeacherActivity.this, EditIntroductionActivity.class);
        intent.putExtra("key", UUID);
        startActivity(intent);
    }

    private void routerAddWeek() {
        Intent intent = new Intent(ViewDetailCourseTeacherActivity.this, AddWeekCourseActivity.class);
        intent.putExtra("key", UUID);
        startActivity(intent);
    }

    private void Init() {
        toolbar = findViewById(R.id.toolbar);
        tv_class_code = findViewById(R.id.tv_class_code);
        bt_edit_introduction = findViewById(R.id.bt_edit_introduction);
        tv_introduction = findViewById(R.id.tv_introduction);
        bt_add_week = findViewById(R.id.bt_add_week);
        refDb = FirebaseDatabase.getInstance().getReference();
        RecyclerView rv_week = findViewById(R.id.rv_week);
        LinearLayoutManager manager = new LinearLayoutManager(ViewDetailCourseTeacherActivity.this, RecyclerView.VERTICAL, false);
        rv_week.setLayoutManager(manager);
        adapter = new WeekTeacherAdapter();
        adapter.setContext(ViewDetailCourseTeacherActivity.this);
        adapter.setAddLesson(this);
        rv_week.setAdapter(adapter);
    }

    @Override
    public void viewLesson(View view, int week, int lesson) {
        Intent intent = new Intent(ViewDetailCourseTeacherActivity.this,ViewLessonActivity.class);
        intent.putExtra("UUID",UUID);
        intent.putExtra("week",week);
        intent.putExtra("lesson",lesson);
        startActivity(intent);
    }

    @Override
    public void addLesson(View view, int position) {
        Intent intent = new Intent(ViewDetailCourseTeacherActivity.this, AddLessonCourseActivity.class);
        intent.putExtra("key", UUID);
        intent.putExtra("position", position);
        startActivity(intent);
    }
}