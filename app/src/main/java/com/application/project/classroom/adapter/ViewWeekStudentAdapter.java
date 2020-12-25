package com.application.project.classroom.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.project.classroom.R;
import com.application.project.classroom.module.AddLesson;
import com.application.project.classroom.object.Lesson;
import com.application.project.classroom.object.Week;

import java.util.ArrayList;
import java.util.List;

public class ViewWeekStudentAdapter extends RecyclerView.Adapter<ViewWeekStudentAdapter.ViewWeekStudentHolder> {
    private List<Week> weeks;
    private Context context;
    private AddLesson addLesson;

    public void setAddLesson(AddLesson addLesson) {
        this.addLesson = addLesson;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setWeeks(List<Week> weeks) {
        this.weeks = weeks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewWeekStudentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_week_student,parent,false);
        return new ViewWeekStudentHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewWeekStudentHolder holder, int position) {
        LessonTitleAdapter adapter = new LessonTitleAdapter();
        holder.rv_lesson.setAdapter(adapter);
        holder.rv_lesson.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        holder.tv_week.setText("Week " + (position + 1) + "");
        holder.tv_description.setText(weeks.get(position).getTitle());
        adapter.setLessons(weeks.get(position).getLessons());
        holder.bt_toggle_show_lesson_down.setOnClickListener(v -> {
            if(holder.rv_lesson.getVisibility()==View.GONE){
                holder.rv_lesson.setVisibility(View.VISIBLE);
                holder.bt_toggle_show_lesson_down.setVisibility(View.GONE);
                holder.bt_toggle_show_lesson_up.setVisibility(View.VISIBLE);
            }
        });
        holder.bt_toggle_show_lesson_up.setOnClickListener(v -> {
            if(holder.rv_lesson.getVisibility()==View.VISIBLE){
                holder.rv_lesson.setVisibility(View.GONE);
                holder.bt_toggle_show_lesson_down.setVisibility(View.VISIBLE);
                holder.bt_toggle_show_lesson_up.setVisibility(View.GONE);
            }
        });
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                addLesson.viewLesson(null,position,viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
            }
        });
        touchHelper.attachToRecyclerView(holder.rv_lesson);
    }

    @Override
    public int getItemCount() {
        if (weeks != null) {
            return weeks.size();
        }
        return 0;
    }

    public static class ViewWeekStudentHolder extends RecyclerView.ViewHolder{
        public TextView tv_week, tv_description;
        public RecyclerView rv_lesson;
        public Button bt_toggle_show_lesson_down,bt_toggle_show_lesson_up;

        public ViewWeekStudentHolder(@NonNull View itemView) {
            super(itemView);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_week = itemView.findViewById(R.id.tv_week);
            rv_lesson = itemView.findViewById(R.id.rv_lesson);
            bt_toggle_show_lesson_down = itemView.findViewById(R.id.bt_toggle_show_lesson_down);
            bt_toggle_show_lesson_up = itemView.findViewById(R.id.bt_toggle_show_lesson_up);
        }
    }


}
