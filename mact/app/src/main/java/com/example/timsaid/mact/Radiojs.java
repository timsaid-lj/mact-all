package com.example.timsaid.mact;
import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.timsaid.mact.radio.RadioActivity;
import com.zhy.http.okhttp.OkHttpUtils;

public class Radiojs {
    RadioActivity radioActivity = new RadioActivity();
    private Activity activity;
    private WebView webView;

    public Radiojs(Activity activity, WebView webView) {
        this.activity = activity;
        this.webView = webView;
    }

    @JavascriptInterface
    public void onFinishActivity() {
        activity.finish();
    }

    /**
     * 开始录音
     * @param msg
     */
    @JavascriptInterface
    public void startrecording(final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity,"开始录音",Toast.LENGTH_SHORT).show();
            }
        });

        radioActivity.fasong();
    }

    /**
     * 结束录音
     * @param msg
     */
    @JavascriptInterface
    public void stoptrecording(final String msg) throws Exception {
        radioActivity.jieshu();
      activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity,"结束录音",Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * 播放录音
     * @param msg
     */
    @JavascriptInterface
    public void playtape(final String msg) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity,"播放录音",Toast.LENGTH_LONG).show();
            }
        });

        radioActivity.play();
    }
}

