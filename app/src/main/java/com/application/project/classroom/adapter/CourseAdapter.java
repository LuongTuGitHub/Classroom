package com.application.project.classroom.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.project.classroom.R;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.module.MenuOnClickItem;
import com.application.project.classroom.module.OnClickItemCourse;
import com.application.project.classroom.object.Course;
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

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseHolder> {

    private List<String> courses;

    private final FirebaseUser fUser;
    private final StorageReference refStg;
    private final DatabaseReference refDb;

    private MenuOnClickItem menuOnClickItem;
    private OnClickItemCourse onClickItemCourse;

    public void setOnClickItemCourse(OnClickItemCourse onClickItemCourse) {
        this.onClickItemCourse = onClickItemCourse;
    }

    public void setMenuOnClickItem(MenuOnClickItem menuOnClickItem) {
        this.menuOnClickItem = menuOnClickItem;
    }

    public CourseAdapter(){
        courses = new ArrayList<>();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refStg = FirebaseStorage.getInstance().getReference();
        refDb = FirebaseDatabase.getInstance().getReference();
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_course,parent,false);
        return new CourseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseHolder holder, int position) {
       refDb.child(Const.COURSE).child(courses.get(position)).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    Course course = snapshot.getValue(Course.class);
                    if(course!=null){
                        refDb.child(Const.ACCOUNT).child(course.getTeacher().hashCode()+"").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.getValue()!=null){
                                    Person person = snapshot.getValue(Person.class);
                                    if(person!=null){
                                        holder.tv_teacher.setText(person.getNameUser());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        holder.iv_course.setOnClickListener(v -> onClickItemCourse.OnClickItem(v,course.getCourseUUID()));
                        holder.itemView.setOnClickListener(v -> onClickItemCourse.OnClickItem(v,course.getCourseUUID()));
                        holder.tv_name_course.setOnClickListener(v -> onClickItemCourse.OnClickItem(v,course.getCourseUUID()));
                        holder.tv_teacher.setOnClickListener(v -> onClickItemCourse.OnClickItem(v,course.getCourseUUID()));

                        holder.tv_name_course.setText(course.getNameCourse());

                        refStg.child(Const.COURSE).child(course.getCourseUUID()+".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(),0, Objects.requireNonNull(task.getResult()).length);
                                holder.iv_course.setImageBitmap(bitmap);
                            }
                        });
                        PopupMenu menu = new PopupMenu(holder.itemView.getContext(),holder.bt_menu);
                        if(course.getTeacher().equals(fUser.getEmail())){
                            menu.inflate(R.menu.menu_course_teacher);
                        }else {
                            menu.inflate(R.menu.menu_course_student);
                        }
                        menu.setOnMenuItemClickListener(item -> menuOnClickItem.OnClickItem(item,position));
                        holder.bt_menu.setOnClickListener(v -> menu.show());
                    }
                }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }

    @Override
    public int getItemCount() {
        if(courses!=null){
            return courses.size();
        }
        return 0;
    }

    public static class CourseHolder extends RecyclerView.ViewHolder{

        public TextView tv_teacher,tv_name_course;
        public ImageView iv_course;
        public Button bt_menu;

        public CourseHolder(@NonNull View itemView) {
            super(itemView);
            setIsRecyclable(false);
            tv_teacher = itemView.findViewById(R.id.tv_teacher_course);
            tv_name_course = itemView.findViewById(R.id.tv_name_course);

            iv_course = itemView.findViewById(R.id.iv_course);
            bt_menu = itemView.findViewById(R.id.bt_menu);

        }
    }
}
