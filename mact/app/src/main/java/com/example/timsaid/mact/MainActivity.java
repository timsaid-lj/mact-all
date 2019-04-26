package com.example.timsaid.mact;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        webView = (WebView) this.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webView.getSettings().setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        /*webView.addJavascriptInterface(new WebPlugin(), "WebPlugin");
        webView.loadUrl(this.getString(R.string.server_url));*/
        webView.addJavascriptInterface(new Radiojs(this,webView),"$App");

        webView.loadUrl("http://172.17.76.118:3000/user/information-collection");


    }
}