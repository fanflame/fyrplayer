//
// Created by fanqiang on 2019-10-09.
//

#ifndef FFMPEGVIDEO_BASETHREAD_H
#define FFMPEGVIDEO_BASETHREAD_H


class BaseThread {
public:
    void start();

    void stop();

    void threadMain();
};


#endif //FFMPEGVIDEO_BASETHREAD_H
