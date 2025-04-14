package com.example.firebase.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.firebase.R;
import com.example.firebase.databinding.ActivityVideoShortBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class VideoShortActivity extends AppCompatActivity {
    private ActivityVideoShortBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    // Giả sử videoId được truyền qua Intent; nếu không có thì dùng một giá trị mẫu
    private String videoId = "PlQ0E3s8IC6cY4pXvgLA";
    // Không gán giá trị mặc định cho videoUrl
    private String videoUrl;

    // Số lượng lượt like/dislike (sẽ cập nhật từ Firestore)
    private long likeCount = 0;
    private long dislikeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoShortBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Nếu user chưa đăng nhập thì chuyển đến LoginActivity
        if (currentUser == null) {
            Toast.makeText(this, "Chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, com.example.firebase.auth.LoginActivity.class));
            finish();
            return;
        }

        // Hiển thị email của user (góc trên phải)
        binding.tvCurrentUserEmail.setText(currentUser.getEmail());

        // Bắt sự kiện click vào avatar để mở ProfileActivity
        binding.imgAvatarTopRight.setOnClickListener(v -> {
            Intent intent = new Intent(VideoShortActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Lấy thông tin video từ Firestore để lấy videoUrl, likeCount, dislikeCount và thông tin uploader
        db.collection("videos").document("PlQ0E3s8IC6cY4pXvgLA").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy videoUrl từ Firebase
                        String url = documentSnapshot.getString("videoUrl");
                        if (url != null && !url.isEmpty()) {
                            videoUrl = url;
                            binding.videoView.setVideoURI(Uri.parse(videoUrl));
                            binding.videoView.start();
                        } else {
                            Toast.makeText(VideoShortActivity.this, "Không tìm thấy videoUrl trong document", Toast.LENGTH_SHORT).show();
                        }

                        // Lấy số lượt like và dislike
                        Long fc = documentSnapshot.getLong("likeCount");
                        Long dfc = documentSnapshot.getLong("dislikeCount");
                        likeCount = (fc != null ? fc : 0);
                        dislikeCount = (dfc != null ? dfc : 0);
                        binding.likeCount.setText(String.valueOf(likeCount));
                        binding.dislikeCount.setText(String.valueOf(dislikeCount));

                        // Lấy thông tin uploader (ví dụ, email) từ Firestore
                        String uploaderId = documentSnapshot.getString("uploaderId");
                        if (uploaderId != null && !uploaderId.isEmpty()) {
                            db.collection("users").document(uploaderId).get()
                                    .addOnSuccessListener(uDoc -> {
                                        if (uDoc.exists()) {
                                            String uploaderEmail = uDoc.getString("email");
                                            String avatarUrl = uDoc.getString("avatarUrl");
                                            binding.tvUploaderEmail.setText(uploaderEmail);

                                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                                Glide.with(this)
                                                        .load(avatarUrl)
                                                        .placeholder(R.drawable.ic_avatar_placeholder)
                                                        .error(R.drawable.ic_avatar_placeholder)
                                                        .into(binding.imgUploaderAvatar);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(VideoShortActivity.this, "Lỗi khi lấy thông tin uploader: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        }
                    } else {
                        Toast.makeText(VideoShortActivity.this, "Video không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(VideoShortActivity.this, "Lỗi get video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("VideoShortActivity", "Error fetching video document", e);
                });

        // Sự kiện Like: cập nhật Firestore, đồng thời cập nhật UI
        binding.btnLike.setOnClickListener(v -> {
            if (videoUrl != null && !videoUrl.isEmpty()) {
                db.collection("videos").document(videoId)
                        .update("likeCount", FieldValue.increment(1))
                        .addOnSuccessListener(aVoid -> {
                            likeCount++;
                            binding.likeCount.setText(String.valueOf(likeCount));
                            Toast.makeText(this, "Đã thích", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Lỗi Like: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
            } else {
                Toast.makeText(this, "Video URL chưa sẵn sàng", Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện Dislike
        binding.btnDislike.setOnClickListener(v -> {
            if (videoUrl != null && !videoUrl.isEmpty()) {
                db.collection("videos").document(videoId)
                        .update("dislikeCount", FieldValue.increment(1))
                        .addOnSuccessListener(aVoid -> {
                            dislikeCount++;
                            binding.dislikeCount.setText(String.valueOf(dislikeCount));
                            Toast.makeText(this, "Không thích", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Lỗi Dislike: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
            } else {
                Toast.makeText(this, "Video URL chưa sẵn sàng", Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện Share: chia sẻ video qua Intent
        binding.btnShare.setOnClickListener(v -> {
            if (videoUrl != null && !videoUrl.isEmpty()) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareText = "Xem video này: " + videoUrl;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(Intent.createChooser(shareIntent, "Chia sẻ video qua"));
            } else {
                Toast.makeText(this, "Video URL chưa sẵn sàng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
