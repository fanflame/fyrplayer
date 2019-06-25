package com.fanyiran.mediaplayer.fyrplayer;

public class VideoInfo {
    private String url;
    private long duration;
    private String trackId;
    private String mime;
    private int displayWidth;
    private int displayHeight;
    private int width;
    private int height;
    private int frameRate;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayWidth(int displayWidth) {
        this.displayWidth = displayWidth;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public void setDisplayHeight(int displayHeight) {
        this.displayHeight = displayHeight;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "url='" + url + '\'' +
                ", duration=" + duration +
                ", trackId='" + trackId + '\'' +
                ", mime='" + mime + '\'' +
                ", displayWidth=" + displayWidth +
                ", displayHeight=" + displayHeight +
                ", width=" + width +
                ", height=" + height +
                ", frameRate=" + frameRate +
                '}';
    }
}
