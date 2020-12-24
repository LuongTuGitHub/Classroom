package com.application.project.classroom.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.project.classroom.R;
import com.application.project.classroom.module.AddLesson;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.object.Lesson;
import com.application.project.classroom.object.Week;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class WeekTeacherAdapter extends RecyclerView.Adapter<WeekTeacherAdapter.WeekTeacherHolder> {

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    private List<Week> weeks;
    private AddLesson addLesson;

    private String UUID;

    private final DatabaseReference refDb;


    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public void setWeeks(List<Week> weeks) {
        this.weeks = weeks;
        notifyDataSetChanged();
    }

    public WeekTeacherAdapter() {
        refDb = FirebaseDatabase.getInstance().getReference();
    }

    public void setAddLesson(AddLesson addLesson) {
        this.addLesson = addLesson;
    }

    @NonNull
    @Override
    public WeekTeacherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_week_teacher, parent, false);
        return new WeekTeacherHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WeekTeacherHolder holder, int position) {
        List<Lesson> lessons = new ArrayList<>();
        LessonTitleAdapter adapter = new LessonTitleAdapter();
        adapter.setLessons(lessons);
        holder.rv_lesson.setAdapter(adapter);
        holder.rv_lesson.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        holder.tv_week.setText("Week " + (position + 1) + "");
        holder.tv_description.setText(weeks.get(position).getTitle());
        holder.bt_add_lesson.setOnClickListener(v -> addLesson.addLesson(v, position));
        refDb.child(Const.COURSE).child(UUID).child("weeks").child(position + "").child("lessons").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    Log.e("â","aaa");
                    Lesson lesson = snapshot.getValue(Lesson.class);
                    if (lesson != null) {
                        lessons.add(lesson);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (weeks != null) {
            return weeks.size();
        }
        return 0;
    }

    public static class WeekTeacherHolder extends RecyclerView.ViewHolder {
        public TextView tv_week, tv_description;
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
