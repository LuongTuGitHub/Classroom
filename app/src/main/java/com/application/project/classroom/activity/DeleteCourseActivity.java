package com.application.project.classroom.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import java.util.List;

public class DeleteCourseActivity extends AppCompatActivity implements AddLesson {

    private Course course;
    private String UUID;
    private Person person;

    private Toolbar toolbar;

    private DatabaseReference refDb;

    private ViewWeekStudentAdapter adapter;

    private Button bt_delete;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_course);

        UUID = getIntent().getStringExtra("key");
        Init();

        AlertDialog.Builder aBuilder = new AlertDialog.Builder(DeleteCourseActivity.this);
        aBuilder.setView(R.layout.view_load);
        dialog = aBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);

        toolbar.setNavigationOnClickListener(v -> finish());
        bt_delete.setOnClickListener(v -> {
            dialog.show();
            if (person.getAssets() >= (course.getPrice() * (course.getPersons().size()))) {
                deleteCourse();
            } else {
                dialog.dismiss();
                Toast.makeText(this, "Total money is not enough", Toast.LENGTH_SHORT).show();
            }
        });
        loadCourse();
    }

    private void Init() {
        toolbar = findViewById(R.id.toolbar);
        refDb = FirebaseDatabase.getInstance().getReference();
        adapter = new ViewWeekStudentAdapter();
        adapter.setAddLesson(this);
        adapter.setContext(DeleteCourseActivity.this);
        RecyclerView rv_week = findViewById(R.id.rv_week);
        rv_week.setLayoutManager(new LinearLayoutManager(DeleteCourseActivity.this, RecyclerView.VERTICAL, false));
        rv_week.setAdapter(adapter);
        bt_delete = findViewById(R.id.bt_delete_course);

    }

    private void deleteCourse() {
        if (course.getPersons() != null) {
            for (int i = 0; i < course.getPersons().size(); i++) {
                refund(course.getPrice(), course.getPersons().get(i));
            }

        }
        List<String> courseList = person.getCourses();
        if (courseList != null) {
            for (int i = 0; i < courseList.size(); i++) {
                if (courseList.get(i).equals(UUID)) {
                    courseList.remove(i);
                    break;
                }
            }
        }
        person.setCourses(courseList);
        int assets = person.getAssets();
        assets -= (course.getPrice() * (course.getPersons().size()));
        person.setAssets(assets);
        refDb.child(Const.ACCOUNT).child(person.getEmailUser().hashCode() + "").setValue(person);
        refDb.child(Const.COURSE).child(UUID).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dialog.dismiss();
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                dialog.dismiss();
            }
        });
    }

    private void refund(int price, String email) {
        refDb.child(Const.ACCOUNT).child(email.hashCode() + "").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Person student = snapshot.getValue(Person.class);
                    if (student != null) {
                        List<String> courseList = student.getCourses();
                        if (courseList != null) {
                            for (int i = 0; i < courseList.size(); i++) {
                                if (courseList.get(i).equals(UUID)) {
                                    courseList.remove(i);
                                    break;
                                }
                            }
                            int assets = student.getAssets();
                            assets += price;
                            student.setAssets(assets);
                            refDb.child(Const.ACCOUNT).child(email.hashCode() + "").setValue(student);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                                    person = snapshot.getValue(Person.class);
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
        Intent intent = new Intent(DeleteCourseActivity.this, ViewLessonActivity.class);
        intent.putExtra("UUID", UUID);
        intent.putExtra("week", week);
        intent.putExtra("lesson", lesson);
        startActivity(intent);
    }

}