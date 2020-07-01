package com.fanyiran.fyrrecorder.recorderview.callback;

import com.fanyiran.fyrrecorder.recorderview.opengl.DirectDrawer;

public interface OnFrameAvailableCallBack {
    void onFrameAvaiable(DirectDrawer directDrawer, float[] mtx, int x, int y, int width, int height, long time);
}
