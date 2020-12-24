package com.application.project.classroom.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.project.classroom.R;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.object.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class SetImageUserActivity extends AppCompatActivity {

    private final static int PERMISSION_CAMERA = 110;
    private final static int PICK_IMAGE = 100;
    private final static int CAPTURE_IMAGE = 90;

    private ImageView iv_user;

    private TextView tv_user;

    private Toolbar toolbar;

    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private StorageReference refStg;

    private Person person;

    private AlertDialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_image_user);

        Init();
        AlertDialog.Builder builder = new AlertDialog.Builder(SetImageUserActivity.this);
        builder.setView(R.layout.view_load);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        toolbar.setNavigationOnClickListener(v -> finish());

        iv_user.setOnClickListener(v -> {
            BottomSheetDialog btDialog = new BottomSheetDialog(SetImageUserActivity.this);
            View view = View.inflate(SetImageUserActivity.this, R.layout.dialog_method_picture, null);
            btDialog.setContentView(view);
            Button bt_camera = view.findViewById(R.id.bt_camera);
            Button bt_gallery = view.findViewById(R.id.bt_gallery);
            bt_camera.setOnClickListener(v1 -> {
                routerCamera();
                btDialog.cancel();
            });
            bt_gallery.setOnClickListener(v12 -> {
                routerGallery();
                btDialog.cancel();
            });
            btDialog.show();
        });

        loadUser();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void routerCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAPTURE_IMAGE);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        }
    }

    private void routerGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA) {
            routerCamera();
        }
    }

    private void Init() {
        iv_user = findViewById(R.id.iv_user);
        tv_user = findViewById(R.id.tv_user);
        toolbar = findViewById(R.id.toolbar);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
    }

    private void loadUser() {
        refDb.child(Const.ACCOUNT).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    person = snapshot.getValue(Person.class);
                    if (person != null) {
                        tv_user.setText(person.getNameUser());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    dialog.show();
                    Uri uri = data.getData();
                    refStg.child(Const.AVATAR).child(person.getUserUUID() + ".png").putFile(uri).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                iv_user.setImageBitmap(bitmap);
                                dialog.dismiss();
                            } catch (IOException e) {
                                e.printStackTrace();
                                dialog.dismiss();
                            }
                        } else {
                            dialog.dismiss();
                        }
                    });
                }
            }
        }

        if (requestCode == CAPTURE_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    dialog.show();
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bytes = stream.toByteArray();
                    refStg.child(Const.AVATAR).child(person.getUserUUID() + ".png").putBytes(bytes).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            iv_user.setImageBitmap(bitmap);
                        } else {
                            dialog.dismiss();
                        }
                    });

                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!dialog.isShowing()) {
            super.onBackPressed();
        }
    }
}