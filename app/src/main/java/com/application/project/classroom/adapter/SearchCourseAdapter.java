package com.application.project.classroom.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.project.classroom.R;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.module.OnClickItemCourse;
import com.application.project.classroom.object.Course;
import com.application.project.classroom.object.Person;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SearchCourseAdapter extends RecyclerView.Adapter<SearchCourseAdapter.SearchCourseHolder> implements Filterable {

    private final StorageReference refStg;
    private final DatabaseReference refDb;

    private List<Course> courses;

    private List<Course> coursesAll;

    private OnClickItemCourse onClickItemCourse;

    public void setOnClickItemCourse(OnClickItemCourse onClickItemCourse) {
        this.onClickItemCourse = onClickItemCourse;
    }

    public SearchCourseAdapter() {
        refStg = FirebaseStorage.getInstance().getReference();
        refDb = FirebaseDatabase.getInstance().getReference();
        courses = new ArrayList<>();
        coursesAll = new ArrayList<>();
    }

    public void setCoursesAll(List<Course> coursesAll) {
        this.coursesAll = coursesAll;
    }

    @NonNull
    @Override
    public SearchCourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_course,parent,false);
        return new SearchCourseHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SearchCourseHolder holder, int position) {
        Course course = courses.get(position);

        holder.v_ratting.setText(course.getRatting()+"");
        holder.tv_name_course.setText(course.getNameCourse());
        holder.tv_price.setText(course.getPrice()+"$");

        refDb.child(Const.ACCOUNT).child(course.getTeacher().hashCode()+"").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    Person person = snapshot.getValue(Person.class);
                    if(person!=null){
                        holder.tv_teacher.setText(person.getNameUser());
                        refStg.child(Const.AVATAR).child(person.getUserUUID()+".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(),0, Objects.requireNonNull(task.getResult()).length);
                                holder.iv_teacher.setImageBitmap(bitmap);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.v_ratting.setOnClickListener(v -> onClickItemCourse.OnClickItem(v,course.getCourseUUID()));
        holder.itemView.setOnClickListener(v -> onClickItemCourse.OnClickItem(v,course.getCourseUUID()));
        holder.iv_teacher.setOnClickListener(v -> onClickItemCourse.OnClickItem(v,course.getCourseUUID()));
        holder.tv_name_course.setOnClickListener(v -> onClickItemCourse.OnClickItem(v,course.getCourseUUID()));
        holder.tv_teacher.setOnClickListener(v -> onClickItemCourse.OnClickItem(v,course.getCourseUUID()));
        holder.tv_price.setOnClickListener(v -> onClickItemCourse.OnClickItem(v,course.getCourseUUID()));
    }

    @Override
    public int getItemCount() {
        if(courses==null){
            return 0;
        }
        return courses.size();
    }

    static class SearchCourseHolder extends RecyclerView.ViewHolder{

        public TextView tv_teacher,tv_name_course,tv_price;
        public Button v_ratting;
        public ImageView iv_teacher;

        public SearchCourseHolder(@NonNull View itemView) {
            super(itemView);

            tv_teacher = itemView.findViewById(R.id.tv_name_teacher);
            tv_name_course = itemView.findViewById(R.id.tv_name_course);
            tv_price = itemView.findViewById(R.id.tv_price);
            v_ratting = itemView.findViewById(R.id.v_ratting);
            iv_teacher = itemView.findViewById(R.id.iv_teacher);

            setIsRecyclable(false);

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Course> result = new ArrayList<>();
                if(constraint.toString().isEmpty()){
                    result = new ArrayList<>(coursesAll);
                }else {
                    for (int i = 0; i < coursesAll.size(); i++) {
                        if(coursesAll.get(i).getNameCourse().toLowerCase().contains(constraint.toString().toLowerCase())||(coursesAll.get(i).getCourseUUID().contains(constraint.toString()))){
                            result.add(coursesAll.get(i));
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = result;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(courses==null){
                    courses = new ArrayList<>();
                }
                courses.clear();
                courses.addAll((Collection<? extends Course>) results.values);
                notifyDataSetChanged();
            }
        };
    }


}
