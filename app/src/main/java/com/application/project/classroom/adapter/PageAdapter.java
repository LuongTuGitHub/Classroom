package com.application.project.classroom.adapter;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.project.classroom.R;
import com.application.project.classroom.object.Page;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.io.IOException;
import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageHolder> {

    private ContentResolver resolver;
    private List<Page> pages;

    public void setPages(List<Page> pages) {
        this.pages = pages;
        notifyDataSetChanged();
    }

    public void setResolver(ContentResolver resolver) {
        this.resolver = resolver;
    }

    @NonNull
    @Override
    public PageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_page_lesson, parent, false);
        return new PageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageHolder holder, int position) {
        if (position == 0) {
            holder.edt_content.setHint(R.string.enter_content);
        }
        if (pages.get(position).getText() != null) {
            holder.edt_content.setText(pages.get(position).getText());
        }
        if (pages.get(position).getIdYoutube() != null) {
            holder.view_youtube.setVisibility(View.VISIBLE);
            holder.view_youtube.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(YouTubePlayer youTubePlayer) {
                    youTubePlayer.loadVideo(pages.get(position).getIdYoutube(), 0);
                }
            });
        }
        if (pages.get(position).getImageUUID() != null) {
            holder.iv_image_page.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(pages.get(position).getImageUUID());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
                holder.iv_image_page.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        holder.edt_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pages.get(position).setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (pages != null) {
            return pages.size();
        }
        return 0;
    }

    public static class PageHolder extends RecyclerView.ViewHolder {
        public EditText edt_content;
        public YouTubePlayerView view_youtube;
        public ImageView iv_image_page;

        public PageHolder(@NonNull View itemView) {
            super(itemView);
            view_youtube = itemView.findViewById(R.id.video_youtube);
            edt_content = itemView.findViewById(R.id.edt_content);
            iv_image_page = itemView.findViewById(R.id.iv_image_page);
            setIsRecyclable(false);
        }
    }
}
