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
import com.application.project.classroom.module.AddLesson;
import com.application.project.classroom.object.Week;

import java.util.List;

public class WeekTeacherAdapter extends RecyclerView.Adapter<WeekTeacherAdapter.WeekTeacherHolder> {

    public WeekTeacherAdapter() {

    }
    private List<Week> weeks;
    private AddLesson addLesson;
    public void setWeeks(List<Week> weeks) {
        this.weeks = weeks;
        notifyDataSetChanged();
    }

    public void setAddLesson(AddLesson addLesson) {
        this.addLesson = addLesson;
    }

    @NonNull
    @Override
    public WeekTeacherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_week_teacher,parent,false);
        return new WeekTeacherHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WeekTeacherHolder holder, int position) {
            holder.tv_week.setText("Week "+(position+1)+"");
            holder.tv_description.setText(weeks.get(position).getTitle());
            holder.bt_add_lesson.setOnClickListener(v -> addLesson.addLesson(v,position));
    }

    @Override
    public int getItemCount() {
        if(weeks!=null){
            return weeks.size();
        }
        return 0;
    }

    public static class WeekTeacherHolder extends RecyclerView.ViewHolder{
        public TextView tv_week,tv_description;
        public RecyclerView rv_lesson;
        public Button bt_add_lesson;
        public WeekTeacherHolder(@NonNull View itemView) {
            super(itemView);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_week = itemView.findViewById(R.id.tv_week);
            rv_lesson = itemView.findViewById(R.id.rv_lesson);
            bt_add_lesson = itemView.findViewById(R.id.bt_add_lesson);
        }
    }
}
