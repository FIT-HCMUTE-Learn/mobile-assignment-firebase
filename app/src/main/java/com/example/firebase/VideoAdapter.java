package com.example.firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.example.firebase.databinding.ItemVideoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import java.util.List;

public class VideoAdapter
        extends RecyclerView.Adapter<VideoAdapter.Holder> {

    public interface ProfileCallback { void onProfileClick(); }

    private List<Video> list;
    private String currentUid;
    private ProfileCallback callback;

    public VideoAdapter(List<Video> list, String uid, ProfileCallback cb) {
        this.list = list;
        this.currentUid = uid;
        this.callback = cb;
    }

    @Override public Holder onCreateViewHolder(ViewGroup p, int i) {
        return new Holder(ItemVideoBinding.inflate(
                LayoutInflater.from(p.getContext()), p,false));
    }

    @Override public void onBindViewHolder(Holder h, int pos) {
        Video v = list.get(pos);
        h.b.tvLikeCount.setText(String.valueOf(v.likes));
        h.b.tvDislikeCount.setText(String.valueOf(v.dislikes));
        h.b.ivUserAvatar.setOnClickListener(_v->callback.onProfileClick());
        h.b.llPosterInfo.setOnClickListener(_v->callback.onProfileClick());

        // load email of poster
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(v.posterUid)
                .child("email")
                .get().addOnSuccessListener(res ->
                        h.b.tvPosterEmail.setText(res.getValue(String.class))
                );

        // TODO: play video in h.b.videoContainer via ExoPlayer, etc.
    }

    @Override public int getItemCount() { return list.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        ItemVideoBinding b;
        Holder(ItemVideoBinding binding) {
            super(binding.getRoot());
            b = binding;
        }
    }
}
