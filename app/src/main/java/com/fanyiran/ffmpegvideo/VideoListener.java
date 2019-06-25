package com.fanyiran.ffmpegvideo;

public interface VideoListener {
    void onError(int errorCode);
    void onPrepared();
    void onComplete();
}
