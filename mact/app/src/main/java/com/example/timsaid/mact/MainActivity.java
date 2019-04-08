package com.example.timsaid.mact;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;

import com.tencent.smtt.sdk.QbSdk;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        webView = (WebView) this.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        // webView.addJavascriptInterface(new WebPlugin(), "WebPlugin");
        //webView.loadUrl(this.getString(R.string.server_url));
        webView.loadUrl("http://192.168.1.5:3000/user/information-collection");
    }
}
