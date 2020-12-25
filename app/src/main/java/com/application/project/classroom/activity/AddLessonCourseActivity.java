package com.application.project.classroom.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.application.project.classroom.R;
import com.application.project.classroom.adapter.PageAdapter;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.object.Course;
import com.application.project.classroom.object.Lesson;
import com.application.project.classroom.object.Page;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddLessonCourseActivity extends AppCompatActivity {
    private final static int PICK_IMAGE = 90;

    private FloatingActionButton bt_add_image,bt_add_id_video;

    private String UUID;
    private int position;
    private Course course;

    private Toolbar toolbar;
    private PageAdapter adapter;
    private RecyclerView rv_lesson_edit;
    private Button bt_add_lesson;
    private EditText edt_title;


    private StorageReference refStg;
    private TextView tv_week;

    private List<Page> pages;

    private AlertDialog dialog;

    private DatabaseReference refDb;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lesson_course);
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(AddLessonCourseActivity.this);
        View view = LayoutInflater.from(AddLessonCourseActivity.this).inflate(R.layout.alert_add_id_youtube,null);
        aBuilder.setView(view);
        dialog = aBuilder.create();
        Button bt_cancel = view.findViewById(R.id.bt_cancel);
        Button bt_confirm = view.findViewById(R.id.bt_confirm);
        EditText edt_id = view.findViewById(R.id.edt_id_video);
        assert bt_cancel != null;
        bt_cancel.setOnClickListener(v -> dialog.dismiss());
        assert bt_confirm != null;
        bt_confirm.setOnClickListener(v -> {
            assert edt_id != null;
            if(edt_id.getText().toString().trim().length()>0){
                pages.get(pages.size()-1).setIdYoutube(edt_id.getText().toString());
                pages.add(new Page(""));
                adapter.setPages(pages);
                rv_lesson_edit.scrollToPosition(pages.size()-1);
            }
            dialog.dismiss();
        });

        setSupportActionBar(toolbar);
        Init();
        UUID = getIntent().getStringExtra("key");
        position = getIntent().getIntExtra("position",-1);
        if(UUID==null||(position==-1)){
            finish();
        }
        tv_week.setText("Week "+(position+1));

        toolbar.setNavigationOnClickListener(v -> finish());
        bt_add_id_video.setOnClickListener(v -> dialog.show());
        bt_add_image.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/");
            startActivityForResult(intent,PICK_IMAGE);
        });
        bt_add_lesson.setOnClickListener(v -> {
            if(edt_title.getText().toString().trim().length()>0){
                addLesson();
            }
        });

        loadCourse();
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

    private void addLesson(){
        if(pages!=null){
            for (int i = 0; i < pages.size() ; i++) {
                String key = java.util.UUID.randomUUID().toString();
                if(pages.get(i).getImageUUID()!=null){
                    refStg.child(Const.COURSE).child(key+".png").putFile(Uri.parse(pages.get(i).getImageUUID()));
                    pages.get(i).setImageUUID(key);
                }
            }
            List<Lesson> lessons= course.getWeeks().get(position).getLessons();
            if(lessons==null){
                lessons = new ArrayList<>();
            }
            lessons.add(new Lesson(edt_title.getText().toString(),null,pages));
            course.getWeeks().get(position).setLessons(lessons);
            refDb.child(Const.COURSE).child(UUID).setValue(course).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    pages.clear();
                    pages.add(new Page());
                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    edt_title.setText("");
                }
            });
        }
    }




    private void Init(){
        adapter = new PageAdapter();
        toolbar = findViewById(R.id.toolbar);
        tv_week = findViewById(R.id.tv_week);
        rv_lesson_edit = findViewById(R.id.rv_lesson_edit);
        LinearLayoutManager manager = new LinearLayoutManager(AddLessonCourseActivity.this,RecyclerView.VERTICAL,false);
        rv_lesson_edit.setLayoutManager(manager);
        pages = new ArrayList<>();
        pages.add(new Page(""));
        adapter.setPages(pages);
        adapter.setResolver(getContentResolver());
        bt_add_image = findViewById(R.id.bt_add_image);
        bt_add_id_video = findViewById(R.id.bt_add_id_video_youtube);
        rv_lesson_edit.setAdapter(adapter);
        bt_add_lesson = findViewById(R.id.bt_add_lesson);
        edt_title = findViewById(R.id.edt_title);
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE){
            if(resultCode==RESULT_OK){
                if(data!=null){
                    pages.get(pages.size()-1).setImageUUID(data.getData().toString());
                    pages.add(new Page(""));
                    adapter.setPages(pages);
                    rv_lesson_edit.scrollToPosition(pages.size()-1);
                }
            }
        }
    }
}