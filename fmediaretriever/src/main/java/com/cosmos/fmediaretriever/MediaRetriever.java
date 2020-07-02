package com.cosmos.fmediaretriever;

public interface MediaRetriever {
    boolean init(String path);

    void getVideoDimensions(int[] dimensions);
}
