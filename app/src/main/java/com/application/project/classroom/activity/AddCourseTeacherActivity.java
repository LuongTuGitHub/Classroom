package com.application.project.classroom.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.project.classroom.R;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.object.Course;
import com.application.project.classroom.object.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
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
import java.util.UUID;

public class AddCourseTeacherActivity extends AppCompatActivity {


    private Toolbar toolbar;


    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private StorageReference refStg;

    private Person person;

    private Button bt_create_class;
    private TextInputEditText edt_name_course, edt_introduction;

    private TextView tv_name_user, tv_email_user;
    private ImageView iv_user;

    private AlertDialog dialog_load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course_teacher);

        AlertDialog.Builder aBuilder = new AlertDialog.Builder(AddCourseTeacherActivity.this);
        aBuilder.setView(R.layout.view_load);
        dialog_load = aBuilder.create();
        dialog_load.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        Init();

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> finish());

        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        tv_email_user.setText(fUser.getEmail());

        if (!checkNetWork()) {
            ConstraintLayout lo_login = findViewById(R.id.layout_add_course);
            Snackbar snackbar = Snackbar.make(lo_login, R.string.not_network, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }

        bt_create_class.setOnClickListener(v -> {
            if(checkNetWork()){
                if((!Objects.requireNonNull(edt_name_course.getText()).toString().isEmpty())&&(!edt_name_course.getText().toString().equals(""))&&(!Objects.requireNonNull(edt_introduction.getText()).toString().isEmpty())&&(!edt_introduction.getText().toString().equals(""))){
                    dialog_load.show();
                    String key = UUID.randomUUID().toString();
                    Course course = new Course(fUser.getEmail(),key,Const.getCourseID(),edt_name_course.getText().toString(),edt_introduction.getText().toString());
                    refDb.child(Const.COURSE).child(key).setValue(course).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            if(person.getCourses()!=null){
                                person.getCourses().add(key);
                            }else {
                                List<String> courseKey = new ArrayList<>();
                                courseKey.add(key);
                                person.setCourses(courseKey);
                            }
                            refDb.child(Const.ACCOUNT).child(Objects.requireNonNull(fUser.getEmail()).hashCode()+"").setValue(person).addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    dialog_load.dismiss();
                                    Intent intent = new Intent();
                                    intent.putExtra("status",true);
                                    setResult(RESULT_OK,intent);
                                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        dialog_load.dismiss();
                    });
                }

            }
        });


        loadUser();

    }

    private void Init() {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refStg = FirebaseStorage.getInstance().getReference();
        refDb = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.toolbar);


        tv_name_user = findViewById(R.id.tv_name_user);
        tv_email_user = findViewById(R.id.tv_email_user);

        iv_user = findViewById(R.id.iv_user);

        bt_create_class = findViewById(R.id.bt_create_class);

        edt_name_course = findViewById(R.id.edt_name_course);
        edt_introduction = findViewById(R.id.edt_introduction);

    }

    private void routerSendFeedback() {
        Intent intent = new Intent(AddCourseTeacherActivity.this, SendFeedbackActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_vs, menu);
        return true;
    }

    private void loadImageUser(String UUID){
        refStg.child(Const.AVATAR).child(UUID+".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Bitmap bitmap  = BitmapFactory.decodeByteArray(task.getResult(),0, Objects.requireNonNull(task.getResult()).length);
                iv_user.setImageBitmap(bitmap);
            }
        });
    }

    private void loadUser(){
        refDb.child(Const.ACCOUNT).child(Objects.requireNonNull(fUser.getEmail()).hashCode()+"").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    person = snapshot.getValue(Person.class);
                    if(person!=null){
                        tv_name_user.setText(person.getNameUser());
                        loadImageUser(person.getUserUUID());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.repos) {
            routerSendFeedback();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!dialog_load.isShowing()) {
            super.onBackPressed();
        }
    }

    private boolean checkNetWork() {
        ConnectivityManager mConnect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnect.getActiveNetworkInfo();
        return (info != null) && (info.isConnected());
    }
}