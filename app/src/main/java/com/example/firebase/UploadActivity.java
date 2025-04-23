package com.example.firebase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.firebase.databinding.ActivityUploadBinding;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.InputStream;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {
    private static final int REQUEST_VIDEO = 200;

    private ActivityUploadBinding b;
    private Uri videoUri;
    private Cloudinary cloudinary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityUploadBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        // Configure Cloudinary with your credentials
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "YOUR_CLOUD_NAME",    // replace with your Cloudinary cloud name
                "api_key",    "449156164113799",
                "api_secret", "s9SvKgx2gAvTy4UimSRLeh6Qg7I"
        ));

        // Pick video
        b.btnSelectVideo.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("video/*");
            startActivityForResult(i, REQUEST_VIDEO);
        });

        // Upload to Cloudinary
        b.btnUpload.setOnClickListener(v -> {
            if (videoUri == null) {
                Toast.makeText(this, "Select a video first", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadToCloudinary();
        });
    }

    private void uploadToCloudinary() {
        b.progressBar.setVisibility(View.VISIBLE);
        // run network work off the main thread
        new Thread(() -> {
            try {
                // Open an InputStream from the selected Uri
                InputStream is = getContentResolver().openInputStream(videoUri);

                // Upload the stream as a video
                @SuppressWarnings("unchecked")
                Map<?,?> result = cloudinary.uploader().upload(
                        is,
                        ObjectUtils.asMap("resource_type", "video")
                );

                // Extract the secure URL
                String secureUrl = (String) result.get("secure_url");

                runOnUiThread(() -> {
                    b.progressBar.setVisibility(View.GONE);
                    Toast.makeText(
                            UploadActivity.this,
                            "Uploaded! URL:\n" + secureUrl,
                            Toast.LENGTH_LONG
                    ).show();
                    // (Optionally) You can now store secureUrl in Firebase Database
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    b.progressBar.setVisibility(View.GONE);
                    Toast.makeText(
                            UploadActivity.this,
                            "Upload failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData();
        }
    }
}
