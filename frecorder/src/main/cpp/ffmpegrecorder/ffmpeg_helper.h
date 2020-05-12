//
// Created by fanyiran on 2019-07-15.
//
#include "ffmpeg_config.h"

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavutil/frame.h>
#include <libavformat/avformat.h>
};

#ifndef FFMPEGVIDEO_FFMPEG_HELPER_H
#define FFMPEGVIDEO_FFMPEG_HELPER_H

class FFmpegHelper {
private:
    AVCodec *codec;
    AVCodecContext *codec_context;
    AVPacket packet;
    AVFrame *frame;
    AVFormatContext *formatContext;
    AVStream * stream;


    int ret;
    FFmpegConfig config;
    Status status;
public:
    int frame_count;
    FFmpegHelper(FFmpegConfig);
    ~FFmpegHelper();

    void init();
    void encode(uint8_t *data_y,uint8_t *data_u,uint8_t *data_v);
    void release();
    Status getStatus();
    void callback(const char *);
};


#endif //FFMPEGVIDEO_FFMPEG_HELPER_H
