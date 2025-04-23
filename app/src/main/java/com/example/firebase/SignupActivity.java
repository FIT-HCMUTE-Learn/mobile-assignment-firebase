package com.example.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.firebase.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding b;
    private FirebaseAuth auth;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        b = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        auth = FirebaseAuth.getInstance();
        b.btnSignup.setOnClickListener(v -> {
            String email = b.etEmail.getText().toString();
            String pass  = b.etPassword.getText().toString();
            auth.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(r -> {
                        Toast.makeText(this, "Signed up!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });
    }
}
