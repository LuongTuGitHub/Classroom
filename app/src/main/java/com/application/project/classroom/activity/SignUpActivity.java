package com.application.project.classroom.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.application.project.classroom.R;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.object.Person;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;

    private AlertDialog dialog_load;

    private Toolbar toolbar;

    private Button bt_sign_up;

    private EditText edt_email, edt_name_user, edt_password, edt_repeat_password;

    private CheckBox cb_teacher, cb_student;

    private DatabaseReference refDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        AlertDialog.Builder aBuilder = new AlertDialog.Builder(SignUpActivity.this);
        aBuilder.setView(R.layout.view_load);
        dialog_load = aBuilder.create();
        dialog_load.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog_load.setCanceledOnTouchOutside(false);

        Init();

        if (checkNetWork()) {
            RelativeLayout lo_login = findViewById(R.id.lo_login);
            Snackbar snackbar = Snackbar.make(lo_login, R.string.not_network, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        bt_sign_up.setOnClickListener(v -> createAccount(edt_email.getText().toString(), edt_password.getText().toString()));

        cb_teacher.setOnClickListener(v -> cb_student.setChecked(!cb_student.isChecked()));

        cb_student.setOnClickListener(v -> cb_teacher.setChecked(!cb_teacher.isChecked()));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkNetWork()) {
            RelativeLayout lo_login = findViewById(R.id.lo_login);
            Snackbar snackbar = Snackbar.make(lo_login, R.string.not_network, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (!dialog_load.isShowing()) {
            super.onBackPressed();
        }
    }

    private void Init() {
        fAuth = FirebaseAuth.getInstance();
        refDb = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.toolbar);

        bt_sign_up = findViewById(R.id.bt_sign_up);

        edt_email = findViewById(R.id.edt_email);
        edt_name_user = findViewById(R.id.edt_name);
        edt_password = findViewById(R.id.edt_password);
        edt_repeat_password = findViewById(R.id.edt_repeat_password);

        cb_teacher = findViewById(R.id.cb_teacher);
        cb_student = findViewById(R.id.cb_student);

    }

    private void createAccount(String email, String password) {
        dialog_load.show();
        if (checkNetWork()) {
            dialog_load.dismiss();
        } else {
            if ((!email.isEmpty()) && (!password.isEmpty()) && (!edt_repeat_password.getText().toString().isEmpty()) && (!edt_name_user.getText().toString().isEmpty())) {
                if (password.length() >= 6) {
                    if (password.equals(edt_repeat_password.getText().toString())) {
                        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String key = UUID.randomUUID().toString();
                                isSuccessful(email, password);
                                Person person = new Person(email, password.hashCode() + "", edt_name_user.getText().toString(), key);
                                person.setWork(getWork());
                                refDb.child(Const.ACCOUNT).child(email.hashCode()+"").setValue(person).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        dialog_load.dismiss();
                                        Toast.makeText(SignUpActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                dialog_load.dismiss();
                            }
                        });
                    } else {
                        dialog_load.dismiss();
                    }
                } else {
                    dialog_load.dismiss();
                }
            } else {
                dialog_load.dismiss();
            }
        }

    }

    private String getWork() {
        if (cb_student.isChecked()) {
            return "student";
        }
        return "teacher";
    }

    private boolean checkNetWork() {
        ConnectivityManager mConnect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnect.getActiveNetworkInfo();
        return (info == null) || (!info.isConnected());
    }

    private void isSuccessful(String email, String password) {
        Toast.makeText(this, "Success !", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        setResult(RESULT_OK, intent);
        dialog_load.dismiss();
    }

}