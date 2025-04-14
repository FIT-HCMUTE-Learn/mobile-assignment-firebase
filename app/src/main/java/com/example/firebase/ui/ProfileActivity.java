package com.example.firebase.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.firebase.R;
import com.example.firebase.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị email từ FirebaseAuth
        binding.tvUserEmailProfile.setText(currentUser.getEmail());

        // Lấy dữ liệu người dùng từ Firestore (sử dụng UID làm document ID)
        String userId = currentUser.getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long totalVideos = documentSnapshot.getLong("totalVideos");
                        Long totalLikes = documentSnapshot.getLong("totalLikes");
                        String avatarUrl = documentSnapshot.getString("avatarUrl");

                        binding.tvTotalVideos.setText("Videos: " + (totalVideos != null ? totalVideos : 0));
                        binding.tvTotalLikes.setText("Total Likes: " + (totalLikes != null ? totalLikes : 0));

                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.ic_avatar_placeholder)
                                    .error(R.drawable.ic_avatar_placeholder)
                                    .into(binding.imgAvatarProfile);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải profile: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

        // Sự kiện chỉnh sửa profile (có thể mở Activity khác để cập nhật thông tin)
        binding.btnEditProfile.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng Edit Profile chưa triển khai", Toast.LENGTH_SHORT).show()
        );
    }
}
