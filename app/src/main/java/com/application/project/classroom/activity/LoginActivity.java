package com.application.project.classroom.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.application.project.classroom.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private final static int ROUTER_SIGN_UP = 2;

    private Button bt_sign_up, bt_login, bt_forget;
    private EditText edt_email, edit_password;
    private CheckBox cb_remember;

    private SharedPreferences refShares;

    private FirebaseAuth fAuth;
    private FirebaseUser fUser;

    private AlertDialog dialog_load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Init();

        AlertDialog.Builder aBuilder = new AlertDialog.Builder(LoginActivity.this);
        aBuilder.setView(R.layout.view_load);
        dialog_load = aBuilder.create();
        dialog_load.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog_load.setCanceledOnTouchOutside(false);

        edt_email.setText(refShares.getString("email", ""));
        edit_password.setText(refShares.getString("password", ""));
        cb_remember.setChecked(refShares.getBoolean("check",false));

        if (checkNetWork()) {
            RelativeLayout lo_login = findViewById(R.id.lo_login);
            Snackbar snackbar = Snackbar.make(lo_login, R.string.not_network, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }

        bt_sign_up.setOnClickListener(v -> routerSignUp());

        bt_login.setOnClickListener(v -> login(edt_email.getText().toString(), edit_password.getText().toString()));

        bt_forget.setOnClickListener(v -> {
            if (!edt_email.getText().toString().isEmpty()) {
                fAuth.sendPasswordResetEmail(edt_email.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Send!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void Init() {
        bt_login = findViewById(R.id.bt_login);
        bt_sign_up = findViewById(R.id.bt_sign_up);
        edit_password = findViewById(R.id.edt_password);
        edt_email = findViewById(R.id.edt_email);
        cb_remember = findViewById(R.id.cb_remember);
        bt_forget = findViewById(R.id.bt_forget_password);
        refShares = getSharedPreferences("data", MODE_PRIVATE);
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
    }

    private boolean checkNetWork() {
        ConnectivityManager mConnect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnect.getActiveNetworkInfo();
        return (info == null) || (!info.isConnected());
    }

    private void routerSignUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivityForResult(intent, ROUTER_SIGN_UP);
    }

    private void routerHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ROUTER_SIGN_UP) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    edt_email.setText(data.getStringExtra("email"));
                    edit_password.setText(data.getStringExtra("password"));
                }
            }
        }

    }

    private void login(String email, String password) {
        dialog_load.show();
        if (checkNetWork()) {
            dialog_load.dismiss();
        } else {
            SharedPreferences.Editor editor = refShares.edit();
            if ((!edt_email.getText().toString().isEmpty()) && (!edit_password.getText().toString().isEmpty())) {
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (cb_remember.isChecked()) {
                            editor.clear();
                            editor.putString("email", email);
                            editor.putString("password", password);
                            editor.putBoolean("check",cb_remember.isChecked());
                            editor.apply();
                        } else {
                            editor.clear();
                            editor.apply();
                        }
                        routerHome();
                    }
                    dialog_load.dismiss();
                });
            } else {
                dialog_load.dismiss();
            }
        }

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
            if(fUser!=null){
                super.onBackPressed();
            }else {
                routerIntroduction();
            }
        }
    }

    private void routerIntroduction(){
        Intent intent = new Intent(LoginActivity.this,IntroductionScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}