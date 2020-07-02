package com.fanyiran.mediaplayer.fyrplayer.callback;

import com.fanyiran.mediaplayer.fyrplayer.VideoInfo;

public interface OnPlayCallback {
    void onGetVideoInfo(VideoInfo videoInfo);
    void onError(int error,String errorContent);

    void onStart();

    void onFinish();
}
