package com.example.firebase;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import com.example.firebase.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding b;
    private FirebaseAuth auth;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        b = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        auth = FirebaseAuth.getInstance();
        b.btnLogin.setOnClickListener(v -> {
            String email = b.etEmail.getText().toString();
            String pass  = b.etPassword.getText().toString();
            auth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(r -> {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });
    }
}
