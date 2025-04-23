package com.example.firebase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.firebase.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK = 100;

    private ActivityProfileBinding b;
    private FirebaseAuth auth;
    private DatabaseReference videosRef;
    private DatabaseReference userRef;
    private FirebaseStorage storage;
    private String profileUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        auth    = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        // 1) Determine which user we're viewing
        profileUid = getIntent().getStringExtra("USER_UID");
        if (profileUid == null || profileUid.isEmpty()) {
            profileUid = auth.getCurrentUser().getUid();
        }

        userRef   = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(profileUid);
        videosRef = FirebaseDatabase.getInstance()
                .getReference("videos");

        loadAvatar();
        countVideos();

        b.btnChangeAvatar.setOnClickListener(v ->
                pickNewAvatar()
        );
    }

    private void loadAvatar() {
        userRef.child("avatarUrl")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snap) {
                        String url = snap.getValue(String.class);
                        if (url != null && !url.isEmpty()) {
                            // Using Glide to load into ImageView
                            Glide.with(ProfileActivity.this)
                                    .load(url)
                                    .placeholder(R.drawable.ic_avatar_placeholder)
                                    .into(b.ivAvatar);
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) { /*no-op*/ }
                });
    }

    private void countVideos() {
        videosRef.orderByChild("posterUid")
                .equalTo(profileUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ds) {
                        long count = ds.getChildrenCount();
                        b.tvVideoCount.setText("Total Videos: " + count);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        Toast.makeText(
                                ProfileActivity.this,
                                "Could not load count: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void pickNewAvatar() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri == null) return;

            // Upload into Storage under "avatars/<uid>"
            StorageReference ref = storage.getReference()
                    .child("avatars")
                    .child(profileUid);
            ref.putFile(uri)
                    .addOnSuccessListener(taskSnapshot ->
                            // Get download URL
                            ref.getDownloadUrl()
                                    .addOnSuccessListener(downloadUri -> {
                                        // Write URL into Realtime DB
                                        userRef.child("avatarUrl")
                                                .setValue(downloadUri.toString(),
                                                        new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError error,
                                                                                   DatabaseReference ref) {
                                                                if (error == null) {
                                                                    Toast.makeText(
                                                                            ProfileActivity.this,
                                                                            "Avatar updated!",
                                                                            Toast.LENGTH_SHORT
                                                                    ).show();
                                                                    // Refresh UI
                                                                    Glide.with(ProfileActivity.this)
                                                                            .load(downloadUri)
                                                                            .placeholder(R.drawable.ic_avatar_placeholder)
                                                                            .into(b.ivAvatar);
                                                                } else {
                                                                    Toast.makeText(
                                                                            ProfileActivity.this,
                                                                            "Save failed: " + error.getMessage(),
                                                                            Toast.LENGTH_LONG
                                                                    ).show();
                                                                }
                                                            }
                                                        }
                                                );
                                    })
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(
                                    this,
                                    "Upload failed: " + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show()
                    );
        }
    }
}
