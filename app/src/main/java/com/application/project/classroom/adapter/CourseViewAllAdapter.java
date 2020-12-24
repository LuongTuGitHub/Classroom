package com.application.project.classroom.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.project.classroom.R;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.module.OnClickItemCourse;
import com.application.project.classroom.object.Course;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CourseViewAllAdapter extends RecyclerView.Adapter<CourseViewAllAdapter.CourseViewAllHolder>{

    private final DatabaseReference refDb;

    private OnClickItemCourse onClickItemCourse;

    public void setOnClickItemCourse(OnClickItemCourse onClickItemCourse) {
        this.onClickItemCourse = onClickItemCourse;
    }

    private List<String> course;

    public void setCourse(List<String> course) {
        this.course = course;
        notifyDataSetChanged();

    }

    public CourseViewAllAdapter() {
        refDb = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public CourseViewAllHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_course_all,parent,false);
        return new CourseViewAllHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewAllHolder holder, int position) {
        refDb.child(Const.COURSE).child(course.get(position)).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    Course course = snapshot.getValue(Course.class);
                    if(course!=null){
                        holder.tv_name_course.setText(course.getNameCourse());
                        holder.tv_introduction.setText(course.getIntroductionCourse());
                        holder.v_ratting.setText(course.getRatting()+"");
                        holder.tv_price.setText(course.getPrice()+"$");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.itemView.setOnClickListener(v -> onClickItemCourse.OnClickItem(v,course.get(position)));
        holder.tv_price.setOnClickListener(v -> onClickItemCourse.OnClickItem(v,course.get(position)));
        holder.tv_introduction.setOnClickListener(v -> onClickItemCourse.OnClickItem(v,course.get(position)));
        holder.tv_name_course.setOnClickListener(v -> onClickItemCourse.OnClickItem(v,course.get(position)));
        holder.v_ratting.setOnClickListener(v -> onClickItemCourse.OnClickItem(v,course.get(position)));
    }

    @Override
    public int getItemCount() {
        if(course==null){
            return 0;
        }
        return course.size();
    }


    public static class CourseViewAllHolder extends RecyclerView.ViewHolder{
        public TextView tv_name_course,tv_introduction,tv_price;
        public Button v_ratting;
        public CourseViewAllHolder(@NonNull View itemView) {
            super(itemView);
            setIsRecyclable(false);
            tv_name_course =itemView.findViewById(R.id.tv_name_course);
            tv_introduction = itemView.findViewById(R.id.tv_introduction);
            tv_price = itemView.findViewById(R.id.tv_price);
            v_ratting = itemView.findViewById(R.id.v_ratting);
        }
    }
}
