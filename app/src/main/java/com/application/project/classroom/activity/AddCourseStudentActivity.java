package com.application.project.classroom.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.application.project.classroom.R;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.object.Course;
import com.application.project.classroom.object.Person;
import com.google.android.material.textfield.TextInputEditText;
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

public class AddCourseStudentActivity extends AppCompatActivity {

    private final static int ADD_COURSE = 100;

    private Toolbar toolbar;

    private TextInputEditText edt_class_code;
    private Button bt_join_class;

    private TextView tv_email_user;
    private TextView tv_name_user;

    private ImageView iv_user;

    private Person person;


    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private StorageReference refStg;

    private List<Course> courses;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course_student);

        courses = new ArrayList<>();

        Init();

        loadAllCourse();

        AlertDialog.Builder aBuilder = new AlertDialog.Builder(AddCourseStudentActivity.this);
        aBuilder.setView(R.layout.view_load);
        dialog = aBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);

        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.repos) {
                routerSendFeedback();
            }
            return true;
        });

        tv_email_user.setText(fUser.getEmail());

        loadUser();

        edt_class_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 6) {
                    bt_join_class.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue_google)));
                } else {
                    bt_join_class.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bt_join_class.setOnClickListener(v -> {
            dialog.show();
            if ((!Objects.requireNonNull(edt_class_code.getText()).toString().isEmpty()) && (!edt_class_code.getText().toString().equals(""))) {
                if (edt_class_code.getText().toString().length() > 6) {
                    if (checkCourse(edt_class_code.getText().toString())) {
                        if (checkPay(edt_class_code.getText().toString())) {
                            if (checkJoin(edt_class_code.getText().toString())) {
                                if (person != null) {
                                    if (checkCourse(person.getCourses(), edt_class_code.getText().toString())) {
                                        addCourse(edt_class_code.getText().toString(), person);
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(AddCourseStudentActivity.this, "Fail!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    dialog.dismiss();
                                }
                            } else {
                                dialog.dismiss();
                            }
                        } else {
                            dialog.dismiss();
                            routerPayCourse(Objects.requireNonNull(edt_class_code.getText()).toString());
                        }
                    } else {
                        dialog.dismiss();
                        Toast.makeText(this, "Code class wrong", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    dialog.dismiss();
                }
            }else {
                dialog.dismiss();
            }
        });

    }

    private boolean checkCourse(List<String> courseUUID, String UUID) {
        if (courses != null) {
            for (int i = 0; i < courses.size(); i++) {
                if (courses.get(i).getCourseID().equals(UUID)) {
                    if (courseUUID != null) {
                        for (int j = 0; j < courseUUID.size(); j++) {
                            if (courseUUID.get(j).equals(UUID)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean checkJoin(String code){
        if(person.getCourses()!=null){
            for (int i = 0; i < courses.size(); i++) {
                if (courses.get(i).getCourseID().equals(code)) {
                    for (int j = 0; j < person.getCourses().size() ; j++) {
                        if(person.getCourses().get(j).equals(courses.get(i).getCourseUUID())){
                            return false;
                        }
                    }
                }
            }

        }

        return true;
    }


    private void addCourse(String code, Person person) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseID().equals(code)) {
                List<String> course = person.getCourses();
                if (course == null) {
                    course = new ArrayList<>();
                }
                person.setCourses(course);
                course.add(courses.get(i).getCourseUUID());
                refDb.child(Const.ACCOUNT).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").setValue(person).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        ifSuccess();
                        Toast.makeText(AddCourseStudentActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                    }
                });
            }
        }
    }

    private void routerPayCourse(String code) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseID().equals(code)) {
                Intent intent = new Intent(AddCourseStudentActivity.this, ViewCourseActivity.class);
                intent.putExtra("key", courses.get(i).getCourseUUID());
                startActivityForResult(intent, ADD_COURSE);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_COURSE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (data.getBooleanExtra("status", false)) {
                        ifSuccess();
                        Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private boolean checkPay(String code) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseID().equals(code)) {
                if (courses.get(i).getPersons() != null) {
                    for (int j = 0; j < courses.get(i).getPersons().size(); j++) {
                        if (courses.get(i).getPersons().get(j).equals(fUser.getEmail())) {
                            return true;
                        }
                    }
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    private boolean checkCourse(String code) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseID().equals(code)) {
                return true;
            }
        }

        return false;
    }

    private void loadImageUser(String UUID) {
        refStg.child(Const.AVATAR).child(UUID + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, Objects.requireNonNull(task.getResult()).length);
                iv_user.setImageBitmap(bitmap);
            }
        });
    }

    private void loadUser() {
        refDb.child(Const.ACCOUNT).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    person = snapshot.getValue(Person.class);
                    if (person != null) {
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

    private void loadAllCourse() {
        refDb.child(Const.COURSE).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    Course course = snapshot.getValue(Course.class);
                    if (course != null) {
                        courses.add(course);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Init() {
        toolbar = findViewById(R.id.toolbar);

        edt_class_code = findViewById(R.id.edt_class_code);

        bt_join_class = findViewById(R.id.bt_join_class);

        tv_name_user = findViewById(R.id.tv_name_user);
        tv_email_user = findViewById(R.id.tv_email_user);

        iv_user = findViewById(R.id.iv_user);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
    }

    private void routerSendFeedback() {
        Intent intent = new Intent(AddCourseStudentActivity.this, SendFeedbackActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_vs, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!dialog.isShowing()) {
            super.onBackPressed();
        }
    }

    private void ifSuccess() {
        Intent intent = new Intent();
        intent.putExtra("status", true);
        setResult(RESULT_OK, intent);
    }
}