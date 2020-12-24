package com.application.project.classroom.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.application.project.classroom.R;
import com.application.project.classroom.module.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class VerifyActivity extends AppCompatActivity {
    private final static int TIME_RELOAD = 2000;

    private Button bt_send;
    private Toolbar toolbar;
    private TextView tv_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        Init();
        setSupportActionBar(toolbar);


        toolbar.setNavigationOnClickListener(v -> {
            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).reload();
            if(FirebaseAuth.getInstance().getCurrentUser()!=null&&(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())){
                routerHome();
            }else if(FirebaseAuth.getInstance().getCurrentUser()!=null&&(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())){
                removeAccount();
                routerLogin();
            }
        });

        bt_send.setOnClickListener(v -> {
            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).reload();
            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(VerifyActivity.this, "Send!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        tv_email.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());

        delayVerity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).reload();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null&&(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())){
            removeAccount();
        }
    }

    @Override
    public void onBackPressed() {
        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).reload();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null&&(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())){
            routerHome();
        }else if(FirebaseAuth.getInstance().getCurrentUser()!=null&&(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())){
            removeAccount();
            routerLogin();
        }
    }

    private void Init(){
        bt_send = findViewById(R.id.bt_verify);
        toolbar = findViewById(R.id.toolbar);
        tv_email = findViewById(R.id.tv_email_user);
    }

    private void routerLogin(){
        Intent intent = new Intent(VerifyActivity.this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void removeAccount(){
        SharedPreferences refShare = getSharedPreferences("data",MODE_PRIVATE);
        SharedPreferences.Editor editor = refShare.edit();
        editor.clear().apply();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference refDb = FirebaseDatabase.getInstance().getReference();
        if(fUser!=null){
            refDb.child(Const.ACCOUNT).child(Objects.requireNonNull(fUser.getEmail()).hashCode()+"").removeValue().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    fUser.delete();
                }
            });

        }

    }

    private void delayVerity(){
        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).reload();
        new Handler().postDelayed(() -> {
            if(FirebaseAuth.getInstance().getCurrentUser()!=null&&(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())){
                routerHome();
            }else if(FirebaseAuth.getInstance().getCurrentUser()!=null&&(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())){
                delayVerity();
            }
        },TIME_RELOAD);
    }

    private void routerHome(){
        Intent intent = new Intent(VerifyActivity.this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}