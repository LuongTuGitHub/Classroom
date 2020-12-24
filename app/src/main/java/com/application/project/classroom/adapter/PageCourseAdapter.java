package com.application.project.classroom.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.project.classroom.R;
import com.application.project.classroom.module.Const;
import com.application.project.classroom.object.Page;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;
import java.util.Objects;

public class PageCourseAdapter extends RecyclerView.Adapter<PageCourseAdapter.PageCourseHolder> {
    private List<Page> pages;
    private final StorageReference refStg;

    public PageCourseAdapter() {
        refStg = FirebaseStorage.getInstance().getReference();
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PageCourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_page_course,parent,false);
        return new PageCourseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageCourseHolder holder, int position) {
        Page page = pages.get(position);
        if(page.getText().isEmpty()||page.getText().equals("")){
            holder.tv_text.setVisibility(View.GONE);
        }else {
            holder.tv_text.setText(page.getText());
        }
        if((page.getImageUUID()==null)||page.getImageUUID().isEmpty()||page.getImageUUID().equals("")){
            holder.iv_image_page.setVisibility(View.GONE);
        }else {
            refStg.child(Const.COURSE).child(page.getImageUUID()+".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(),0, Objects.requireNonNull(task.getResult()).length);
                    holder.iv_image_page.setImageBitmap(bitmap);
                }
            });
        }
        if((page.getIdYoutube()==null)||page.getIdYoutube().isEmpty()||page.getIdYoutube().equals("")){
            holder.view_youtube.setVisibility(View.GONE);
        }else {
            holder.view_youtube.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(YouTubePlayer youTubePlayer) {
                    youTubePlayer.loadVideo(page.getIdYoutube(),0);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if(pages!=null){
            return pages.size();
        }
        return 0;
    }

    public static class  PageCourseHolder extends RecyclerView.ViewHolder{
        public TextView tv_text;
        public ImageView iv_image_page;
        public YouTubePlayerView view_youtube;
        public PageCourseHolder(@NonNull View itemView) {
            super(itemView);
            tv_text = itemView.findViewById(R.id.tv_text);
            iv_image_page = itemView.findViewById(R.id.iv_image_page);
            view_youtube = itemView.findViewById(R.id.view_youtube);
        }
    }
}
