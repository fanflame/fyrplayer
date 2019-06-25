package com.fanyiran.ffmpegvideo.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DefaultExecutor {
    static private Executor executor;

    static private class DefaultExecutorHolder {
        static DefaultExecutor defaultExecutor = new DefaultExecutor();
    }

    private DefaultExecutor(){
        executor = Executors.newSingleThreadExecutor();
    }

    static public Executor getDefaultExecutor() {
        return DefaultExecutorHolder.defaultExecutor.executor;
    }
}
