package com.application.project.classroom.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.application.project.classroom.R;
import com.application.project.classroom.adapter.SearchCourseAdapter;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.module.OnClickItemCourse;
import com.application.project.classroom.object.Course;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchCourseActivity extends AppCompatActivity implements OnClickItemCourse {

    private List<Course> courses;

    private SearchCourseAdapter adapter;

    private EditText edt_content_search;

    private RecyclerView rv_course;

    private DatabaseReference refDb;

    private Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_course);

        Init();

        loadCourse();

        toolbar.setNavigationOnClickListener(v -> finish());


        rv_course.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(SearchCourseActivity.this,RecyclerView.VERTICAL,false);
        rv_course.setLayoutManager(manager);
        rv_course.setAdapter(adapter);
        adapter.setOnClickItemCourse(this);

        edt_content_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void OnClickItem(View view, String courseUUID) {
        Intent intent = new Intent(SearchCourseActivity.this,ViewCourseActivity.class);
        intent.putExtra("key",courseUUID);
        startActivity(intent);
    }

    private void Init(){
        courses = new ArrayList<>();

        adapter = new SearchCourseAdapter();
        adapter.setCoursesAll(courses);

        edt_content_search = findViewById(R.id.edt_content_search);

        rv_course = findViewById(R.id.rv_course);

        toolbar = findViewById(R.id.toolbar);

        refDb = FirebaseDatabase.getInstance().getReference();

    }

    private void loadCourse(){
        refDb.child(Const.COURSE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getValue()!=null){
                    Course course = snapshot.getValue(Course.class);
                    if(course!=null){
                        courses.add(course);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getValue()!=null){
                    Course course = snapshot.getValue(Course.class);
                    if(course!=null){
                        for (int i = 0; i < courses.size(); i++) {
                            if(course.getCourseUUID().equals(courses.get(i).getCourseUUID())){
                                courses.set(i,course);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    Course course = snapshot.getValue(Course.class);
                    if(course!=null){
                        for (int i = 0; i < courses.size(); i++) {
                            if(course.getCourseUUID().equals(courses.get(i).getCourseUUID())){
                                courses.remove(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}