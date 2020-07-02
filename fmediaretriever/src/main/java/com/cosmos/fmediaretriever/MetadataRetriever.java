package com.cosmos.fmediaretriever;

import android.media.MediaMetadataRetriever;

import com.fanyiran.utils.LogUtil;

import java.io.File;

public class MetadataRetriever implements MediaRetriever {
    private static final String TAG = "MetadataRetriever";
    private MediaMetadataRetriever mediaMetadataRetriever;

    public boolean init(String path) {
        if (path == null) {
            return false;
        }
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        mediaMetadataRetriever = new MediaMetadataRetriever();
        // TODO: 2020/7/2 放到异步线程？
        mediaMetadataRetriever.setDataSource(path);
        return true;
    }

    @Override
    public void getVideoDimensions(int[] dimensions) {
        if (dimensions == null) {
            return;
        }
        String date = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
        String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String tracks = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS);
        String mimeType = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        String width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String bitRate = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
        String location = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION);
        String rotation = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        String frameRate = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE);
        String frameCount = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
        String hasImage = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_IMAGE);

        LogUtil.v(TAG, String.format("date:%s\n" +
                        "duration:%s\n" +
                        "tracks:%s\n" +
                        "mimeType:%s\n" +
                        "width:%s\n" +
                        "height:%s\n" +
                        "bitRate:%s\n" +
                        "location:%s\n" +
                        "rotation:%s\n" +
                        "frameRate:%s\n" +
                        "frameCount:%s\n" +
                        "hasImage:%s\n"
                , date, duration, tracks, mimeType, width, height, bitRate, location, rotation, frameRate, frameCount, hasImage));
    }
}
