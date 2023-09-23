package com.trungcoder.youtubeforcar;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class JavaScriptInterface {
    @JavascriptInterface
    public void onVideoClicked(String url) {
        // Handle the videoId (e.g., load it in your YouTube player)
        Log.d("youtube url","got an url");
    }
}
