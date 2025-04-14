package com.example.firebase.storage;

import android.net.Uri;
import com.example.firebase.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseStorageHelper {
    public static void uploadVideo(Uri videoUri, String fileName, OnUploadListener listener) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference ref = FirebaseStorage.getInstance()
                .getReference(Constants.FIREBASE_STORAGE_PATH + userId + "/" + fileName);

        ref.putFile(videoUri)
                .addOnSuccessListener(taskSnapshot ->
                        ref.getDownloadUrl().addOnSuccessListener(uri ->
                                listener.onSuccess(uri.toString())
                        )
                )
                .addOnFailureListener(listener::onFailure);
    }

    public interface OnUploadListener {
        void onSuccess(String url);
        void onFailure(Exception e);
    }
}
