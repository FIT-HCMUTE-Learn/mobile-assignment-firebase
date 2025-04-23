package com.example.firebase;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.example.firebase.databinding.ActivityWebviewBinding;

public class WebViewActivity extends AppCompatActivity {
    private ActivityWebviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.setWebViewClient(new WebViewClient());  // per WebViewClient.pdf :contentReference[oaicite:8]{index=8}&#8203;:contentReference[oaicite:9]{index=9}
        binding.webview.loadUrl("https://your-responsive-site.example.com");
    }
}
