package com.application.project.classroom.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.project.classroom.R;
import com.application.project.classroom.object.Lesson;

import java.util.List;

public class LessonTitleAdapter extends RecyclerView.Adapter<LessonTitleAdapter.LessonTitleHolder> {
    private List<Lesson> lessons;
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LessonTitleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_lesson,parent,false);
        return new LessonTitleHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LessonTitleHolder holder, int position) {
        holder.tv_lesson.setText("Lesson "+(position+1));
        holder.tv_title.setText(lessons.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        if(lessons!=null){
            return lessons.size();
        }
        return 0;
    }

    public static class LessonTitleHolder extends RecyclerView.ViewHolder{
        public TextView tv_title,tv_lesson;
        public LessonTitleHolder(@NonNull View itemView) {
            super(itemView);
            tv_title =itemView.findViewById(R.id.tv_lesson_title);
            tv_lesson = itemView.findViewById(R.id.tv_lesson);
        }
    }
}
