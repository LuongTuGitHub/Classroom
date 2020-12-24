package com.application.project.classroom.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.project.classroom.R;
import com.application.project.classroom.adapter.WeekCourseAdapter;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.object.Course;
import com.application.project.classroom.object.Person;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewCourseActivity extends AppCompatActivity {

    private Person person;
    private List<String> courses;
    private Course course;
    private String key;
    private WeekCourseAdapter adapter;


    private Toolbar toolbar;

    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private StorageReference refStg;

    private ImageView iv_teacher;
    private TextView tv_title, tv_teacher, tv_email_teacher, tv_introduction, tv_title_pay, tv_teacher_pay, tv_introduction_pay, tv_price;
    private Button v_ratting, v_ratting_pay, bt_view_all_course, bt_pay_course;

    private AlertDialog dialog;

    private RecyclerView rv_week;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course);

        AlertDialog.Builder aBuilder = new AlertDialog.Builder(ViewCourseActivity.this);
        aBuilder.setView(R.layout.view_load);
        dialog = aBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);


        key = getIntent().getStringExtra("key");

        Init();

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> finish());

        bt_pay_course.setOnClickListener(v -> {
            if (person.getAssets() >= course.getPrice()) {
                dialog.show();
                List<String> coursesUUID = person.getCourses();
                if (coursesUUID == null) {
                    coursesUUID = new ArrayList<>();
                }
                coursesUUID.add(course.getCourseUUID());
                person.setAssets(person.getAssets() - course.getPrice());
                person.setCourses(coursesUUID);
                refDb.child(Const.ACCOUNT).child(person.getEmailUser().hashCode() + "").setValue(person).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> people = course.getPersons();
                        if (people == null) {
                            people = new ArrayList<>();
                        }
                        people.add(fUser.getEmail());
                        course.setPersons(people);
                        refDb.child(Const.COURSE).child(course.getCourseUUID()).setValue(course).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                payTeacher();
                                dialog.dismiss();
                                Intent intent = new Intent();
                                intent.putExtra("status", true);
                                setResult(RESULT_OK, intent);
                                bt_pay_course.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        dialog.dismiss();
                    }
                });
            }
        });

        bt_view_all_course.setOnClickListener(v -> {
            if (getIntent().getBooleanExtra("status", false)) {
                finish();
            } else {
                routerAllCourse();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void payTeacher() {
        refDb.child(Const.ACCOUNT).child(course.getTeacher().hashCode() + "").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Person person = snapshot.getValue(Person.class);
                    if (person != null) {
                        person.setAssets(person.getAssets() + course.getPrice());
                        refDb.child(Const.ACCOUNT).child(course.getTeacher().hashCode() + "").setValue(person);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Init() {


        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();

        adapter = new WeekCourseAdapter();
        tv_price = findViewById(R.id.tv_price);
        v_ratting = findViewById(R.id.v_ratting);
        v_ratting_pay = findViewById(R.id.v_ratting_pay_course);
        toolbar = findViewById(R.id.toolbar);
        iv_teacher = findViewById(R.id.iv_teacher);

        bt_view_all_course = findViewById(R.id.bt_view_all_course);
        bt_pay_course = findViewById(R.id.bt_pay_course);

        tv_title = findViewById(R.id.tv_name);
        tv_teacher = findViewById(R.id.tv_teacher);
        tv_email_teacher = findViewById(R.id.tv_email_teacher);
        tv_introduction = findViewById(R.id.tv_introduction);

        tv_title_pay = findViewById(R.id.tv_title_course_bottom_sheet);
        tv_teacher_pay = findViewById(R.id.tv_teacher_name_bottom_sheet);
        tv_introduction_pay = findViewById(R.id.tv_introduction_bottom_sheet);

        LinearLayoutManager manager = new LinearLayoutManager(ViewCourseActivity.this, RecyclerView.VERTICAL, false);

        rv_week = findViewById(R.id.rv_week);
        rv_week.setLayoutManager(manager);
        rv_week.setAdapter(adapter);

        courses = new ArrayList<>();


        loadUser();
    }


    private void loadUser() {
        refDb.child(Const.ACCOUNT).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Person _person = snapshot.getValue(Person.class);
                    if (_person != null) {
                        person = _person;
                        courses = _person.getCourses();
                        loadCourse(key);
                        if (courses != null) {
                            for (int i = 0; i < courses.size(); i++) {
                                if (courses.get(i).equals(key)) {
                                    bt_pay_course.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void routerAllCourse() {
        Intent intent = new Intent(ViewCourseActivity.this, ViewAllCourseTeacherActivity.class);
        intent.putExtra("email", course.getTeacher());
        startActivity(intent);
    }

    private void loadCourse(String key) {
        refDb.child(Const.COURSE).child(key).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    course = snapshot.getValue(Course.class);
                    if (course != null) {
                        Log.e("CODE", course.getCourseID());
                        tv_title.setText(course.getNameCourse());
                        tv_title_pay.setText(course.getNameCourse());

                        if (course.getWeeks() != null) {
                            adapter.setWeeks(course.getWeeks());
                        }
                        tv_introduction.setText(course.getIntroductionCourse());
                        tv_introduction_pay.setText(course.getIntroductionCourse());

                        v_ratting.setText(course.getRatting() + "");
                        v_ratting_pay.setText(course.getRatting() + "");

                        tv_price.setText(course.getPrice() + "$");

                        if (course.getPersons() != null) {
                            for (int i = 0; i < course.getPersons().size(); i++) {
                                if (course.getPersons().get(i).equals(fUser.getEmail())) {
                                    bt_pay_course.setVisibility(View.GONE);
                                }
                            }
                        }
                        refDb.child(Const.ACCOUNT).child(course.getTeacher().hashCode() + "").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    Person person = snapshot.getValue(Person.class);
                                    if (person != null) {
                                        loadTeacher(person);
                                        tv_teacher_pay.setText(person.getNameUser());
                                        tv_teacher.setText(person.getNameUser());

                                        tv_email_teacher.setText(person.getEmailUser());
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

    private void loadTeacher(Person person) {
        refStg.child(Const.AVATAR).child(person.getUserUUID() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, Objects.requireNonNull(task.getResult()).length);
                iv_teacher.setImageBitmap(bitmap);
            }
        });
    }

}