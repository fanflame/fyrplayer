//
// Created by fanyiran on 2019-07-15.
//

#ifndef FFMPEGVIDEO_FFMPEG_CONFIG_H
#define FFMPEGVIDEO_FFMPEG_CONFIG_H

#include "ffmpeg_status.h"
#include <stdarg.h>
class FFmpegConfig {
public:
    int i_frame_interval;
    int encoding_bit_rate;
    int frame_rate;
    int width;
    int height;
    const char *file_name;
    volatile int status = STATUS_UNKNOW;
    void (*callback)(void*, int, const char*, va_list);
};
#endif //FFMPEGVIDEO_FFMPEG_CONFIG_H
