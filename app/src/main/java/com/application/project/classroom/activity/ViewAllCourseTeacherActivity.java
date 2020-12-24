package com.application.project.classroom.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.project.classroom.R;
import com.application.project.classroom.adapter.CourseViewAllAdapter;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.module.OnClickItemCourse;
import com.application.project.classroom.object.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class ViewAllCourseTeacherActivity extends AppCompatActivity implements OnClickItemCourse {

    private String teacher;
    private List<String> course;
    private CourseViewAllAdapter allAdapter;

    private Toolbar toolbar;
    private Button v_ratting;
    private RecyclerView rv_course;

    private Person person;

    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private StorageReference refStg;

    private TextView tv_name_user;
    private TextView tv_email_user;
    private ImageView iv_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_course_teacher);

        teacher = getIntent().getStringExtra("email");

        if (teacher == null) {
            finish();
        }

        Init();
        loadUser();
        loadTeacher();


        toolbar.setNavigationOnClickListener(v -> finish());

        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);

    }

    private void loadUser() {
        refDb.child(Const.ACCOUNT).child(Objects.requireNonNull(fUser.getEmail()).hashCode()+"").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    Person people = snapshot.getValue(Person.class);
                    if(people!=null){
                        tv_name_user.setText(people.getNameUser());
                        tv_email_user.setText(people.getEmailUser());
                        refStg.child(Const.AVATAR).child(people.getUserUUID()+".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(),0, Objects.requireNonNull(task.getResult()).length);
                                iv_user.setImageBitmap(bitmap);
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

    private void loadTeacher() {
        refDb.child(Const.ACCOUNT).child(teacher.hashCode() + "").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    person = snapshot.getValue(Person.class);
                    course.clear();
                    if (person != null) {
                        toolbar.setTitle(person.getNameUser());
                        toolbar.setSubtitle(person.getEmailUser());
                        v_ratting.setText(person.getRatting() + "");
                        course = person.getCourses();
                        allAdapter.setCourse(course);
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
        rv_course = findViewById(R.id.rv_course);
        course = new ArrayList<>();
        allAdapter = new CourseViewAllAdapter();
        allAdapter.setOnClickItemCourse(this);
        LinearLayoutManager manager = new LinearLayoutManager(ViewAllCourseTeacherActivity.this, RecyclerView.VERTICAL, false);
        rv_course.setLayoutManager(manager);
        rv_course.setAdapter(allAdapter);
        toolbar = findViewById(R.id.toolbar);
        v_ratting = findViewById(R.id.v_ratting);
        tv_name_user = findViewById(R.id.tv_name_user);
        tv_email_user = findViewById(R.id.tv_email_user);
        iv_user = findViewById(R.id.iv_user);

    }

    private void routerSendFeedback() {
        Intent intent = new Intent();
        intent.setClass(ViewAllCourseTeacherActivity.this, SendFeedbackActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_vs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.repos) {
            routerSendFeedback();
        }
        return true;
    }

    @Override
    public void OnClickItem(View view, String courseUUID) {
        Intent intent = new Intent(ViewAllCourseTeacherActivity.this, ViewCourseActivity.class);
        intent.putExtra("key", courseUUID);
        intent.putExtra("status",true);
        startActivity(intent);
    }
}