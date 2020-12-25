package com.application.project.classroom.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.project.classroom.R;
import com.application.project.classroom.adapter.CourseAdapter;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.module.MenuOnClickItem;
import com.application.project.classroom.module.OnClickItemCourse;
import com.application.project.classroom.object.Course;
import com.application.project.classroom.object.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import java.util.UUID;

public class HomeActivity extends AppCompatActivity implements MenuOnClickItem, OnClickItemCourse {

    private final static int ADD_COURSE = 100;

    private Toolbar toolbar;

    private DrawerLayout dl_home;

    private ImageView iv_user;

    private FirebaseAuth fAuth;
    private FirebaseUser fUser;
    private StorageReference refStg;
    private DatabaseReference refDb;

    private NavigationView nv_menu;

    private SharedPreferences refShare;

    private List<String> courses;

    private CourseAdapter courseAdapter;

    private RecyclerView rv_course;

    private Person person;


    private FloatingActionButton bt_add;

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Init();

        setSupportActionBar(toolbar);

        if (!checkNetWork()) {
            ConstraintLayout lo_login = findViewById(R.id.lo_home);
            Snackbar snackbar = Snackbar.make(lo_login, R.string.not_network, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }
        if(!fUser.isEmailVerified()){
            Intent intent = new Intent(HomeActivity.this,VerifyActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }


        toolbar.setNavigationOnClickListener(v -> dl_home.openDrawer(GravityCompat.START));

        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        nv_menu.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.setting:
                    routerSetting();
                    break;
                case R.id.help:
                    break;
                case R.id.log_out:
                    routerLogOut();
                    break;
            }
            return true;
        });
        loadUser();

        rv_course.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(HomeActivity.this, RecyclerView.VERTICAL, false);
        rv_course.setLayoutManager(manager);
        courseAdapter.setOnClickItemCourse(this);

        rv_course.setAdapter(courseAdapter);


        bt_add.setOnClickListener(v -> {
            if(person!=null){
                if(person.getWork().equals(Const.TEACHER)){
                    routerCourseTeacher();
                }else if(person.getWork().equals(Const.STUDENT)){
                    routerCourseStudent();
                }
            }
        });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean OnClickItem(MenuItem menu, int position) {
        switch (menu.getItemId()){
            case R.id.cancel_course:
                cancelCourse(courses.get(position));
                break;
            case R.id.edit_course:
                routerViewDetailCourseTeacher(courses.get(position));
                break;
            case R.id.delete_course:
                routerDeleteCourse(courses.get(position));
                break;
        }

        return true;
    }
    private void routerDeleteCourse(String UUID){
        Intent intent = new Intent(HomeActivity.this,DeleteCourseActivity.class);
        intent.putExtra("key",UUID);
        startActivity(intent);
    }
    @Override
    public void OnClickItem(View view, String courseUUID) {
        if(person.getWork().equals(Const.STUDENT)){
            routerViewDetailCourseStudent(courseUUID);
        }else {
            routerViewDetailCourseTeacher(courseUUID);
        }
    }
    private void routerViewDetailCourseStudent(String UUID){
        Intent intent = new Intent(HomeActivity.this,ViewDetailCourseStudentActivity.class);
        intent.putExtra("key",UUID);
        startActivity(intent);

    }
    private void routerViewDetailCourseTeacher(String UUID){
        Intent intent = new Intent(HomeActivity.this,ViewDetailCourseTeacherActivity.class);
        intent.putExtra("key",UUID);
        startActivity(intent);
    }

    private void routerCourseTeacher(){
        Intent intent = new Intent(HomeActivity.this,AddCourseTeacherActivity.class);
        startActivityForResult(intent,ADD_COURSE);
    }
    private void routerCourseStudent(){
        Intent intent = new Intent(HomeActivity.this,AddCourseStudentActivity.class);
        startActivityForResult(intent,ADD_COURSE);
    }

    private void routerSetting() {
        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ADD_COURSE){
            if(resultCode==RESULT_OK){
                if(data!=null){
                    if(data.getBooleanExtra("status",false)){
                        refresh();
                    }
                }
            }
        }
    }

    private void Init() {
        toolbar = findViewById(R.id.toolbar);

        iv_user = findViewById(R.id.iv_user);

        dl_home = findViewById(R.id.dl_home);

        nv_menu = findViewById(R.id.nv_menu);

        bt_add = findViewById(R.id.bt_add_course);

        refShare = getSharedPreferences("data", MODE_PRIVATE);

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();

        rv_course = findViewById(R.id.rv_course);
        courses = new ArrayList<>();

        courseAdapter = new CourseAdapter();
        courseAdapter.setMenuOnClickItem(this);
    }

    private void cancelCourse(String UUID){
        if(person.getCourses()!=null){
            for (int i = 0; i < person.getCourses().size(); i++) {
                if(person.getCourses().get(i).equals(UUID)){
                    person.getCourses().remove(i);
                    refDb.child(Const.ACCOUNT).child(Objects.requireNonNull(fUser.getEmail()).hashCode()+"").setValue(person);
                    courseAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }



    private boolean checkNetWork() {
        ConnectivityManager mConnect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnect.getActiveNetworkInfo();
        return (info != null) && (info.isConnected());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar_home, menu);
        return true;
    }

    private void routerSendFeedback(){
        Intent intent = new Intent(HomeActivity.this,SendFeedbackActivity.class);
        startActivity(intent);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                break;
            case R.id.repos:
                routerSendFeedback();
                break;
            case R.id.search:
                routerSearch();
                break;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void routerSearch(){
        Intent intent = new Intent(HomeActivity.this,SearchCourseActivity.class);
        startActivity(intent);
    }

    private void routerLogOut() {
        refShare.edit().clear().apply();
        fAuth.signOut();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void refresh() {
        loadUser();
    }

    private void loadUser() {
        refDb.child(Const.ACCOUNT).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    person = snapshot.getValue(Person.class);
                    if (person != null) {
                        courseAdapter.setCourses(person.getCourses());
                        courses = person.getCourses();
                        refStg.child(Const.AVATAR).child(person.getUserUUID() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, Objects.requireNonNull(task.getResult()).length);
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

}