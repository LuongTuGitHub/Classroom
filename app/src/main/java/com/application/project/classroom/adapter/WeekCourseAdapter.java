package com.application.project.classroom.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.project.classroom.R;
import com.application.project.classroom.object.Week;

import java.util.List;

public class WeekCourseAdapter extends RecyclerView.Adapter<WeekCourseAdapter.WeekCourseHolder> {

    private List<Week> weeks;

    public void setWeeks(List<Week> weeks) {
        this.weeks = weeks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WeekCourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_week_course,parent,false);
        return new WeekCourseHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WeekCourseHolder holder, int position) {
        holder.tv_week.setText("Week "+(position+1));
        holder.tv_description.setText(weeks.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        if(weeks!=null){
            return weeks.size();
        }
        return 0;
    }

    public static class WeekCourseHolder extends RecyclerView.ViewHolder{
        public TextView tv_week,tv_description;
        public WeekCourseHolder(@NonNull View itemView) {
            super(itemView);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_week = itemView.findViewById(R.id.tv_week);
        }
    }
}
