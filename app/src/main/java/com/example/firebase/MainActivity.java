package com.example.firebase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.firebase.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding b;
    private List<Video> videos = new ArrayList<>();
    private VideoAdapter adapter;
    private DatabaseReference db;
    private FirebaseAuth auth;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser()==null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish(); return;
        }

        // RecyclerView setup
        adapter = new VideoAdapter(videos, auth.getCurrentUser().getUid(), this::openProfile);
        b.recycler.setLayoutManager(new LinearLayoutManager(this));
        b.recycler.setAdapter(adapter);

        db = FirebaseDatabase.getInstance().getReference("videos");
        db.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot ds) {
                videos.clear();
                for (DataSnapshot c: ds.getChildren()) {
                    videos.add(c.getValue(Video.class));
                }
                adapter.notifyDataSetChanged();
            }
            public void onCancelled(DatabaseError e) {}
        });

        b.fabUpload.setOnClickListener(v ->
                startActivity(new Intent(this, UploadActivity.class))
        );
    }

    private void openProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
    }
}
