//
// Created by fanqiang on 2019-10-09.
//

#ifndef FFMPEGVIDEO_ILOG_H
#define FFMPEGVIDEO_ILOG_H

#include "android/log.h"

class ILog {
public:
    static void LOGI() {
        __android_log_print();
    }
};


#endif //FFMPEGVIDEO_ILOG_H
