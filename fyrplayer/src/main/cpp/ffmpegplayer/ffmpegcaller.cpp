//
// Created by fanyiran on 2019-06-13.
//

#include <jni.h>
#include <string>

extern "C" {
#include <android/native_window_jni.h>
#include <libavutil/avutil.h>
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>

JNIEXPORT void JNICALL
Java_com_fanyiran_ffmpegvideo_FFVideoPlayer_render(JNIEnv *env, jobject thiz, jstring url,
                                                   jobject surface) {
    const char *videoUrl = env->GetStringUTFChars(url, 0);
    // 注册。
    avcodec_register_all();
    // 打开地址并且获取里面的内容  avFormatContext是内容的一个上下文
    AVFormatContext *avFormatContext = avformat_alloc_context();
    avformat_open_input(&avFormatContext, videoUrl, NULL, NULL);
    avformat_find_stream_info(avFormatContext, NULL);
    // 找出视频流
    int video_index = -1;
    for (int i = 0; i < avFormatContext->nb_streams; ++i) {
        if (avFormatContext->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
            video_index = i;
        }
    }

    // 解码  转换  绘制
    // 获取解码器上下文
    AVCodecContext *avCodecContext = avFormatContext->streams[video_index]->codec;
    // 获取解码器
    AVCodec *avCodec = avcodec_find_decoder(avCodecContext->codec_id);
    //打开解码器
    if (avcodec_open2(avCodecContext, avCodec, NULL) < 0) {
        //打开失败
        return;
    }
    // 申请AVPacket和AVFrame，
    // 其中AVPacket的作用是：保存解码之前的数据和一些附加信息，如显示时间戳（pts）、解码时间戳（dts）、数据时长，所在媒体流的索引等；
    // AVFrame的作用是：存放解码过后的数据。
    AVPacket *avPacket = static_cast<AVPacket *>(av_malloc(sizeof(AVPacket)));
    av_init_packet(avPacket);
    // 分配一个AVFrame结构体，AVFrame结构体一般用于存储原始数据，指向解码后的原始帧
    AVFrame *avFrame = av_frame_alloc();
    //分配一个AVFrame结构体，指向存放转换后的rgb后的帧
    AVFrame *rgb_frame = av_frame_alloc();
    // rgb_frame是一个缓存区域，所以需要设置
    // 缓存区
    uint8_t *out_butter = static_cast<uint8_t *>(av_malloc(
            avpicture_get_size(AV_PIX_FMT_RGBA, avCodecContext->width, avCodecContext->height)));
    // 与缓冲区相关连，设置rgb_frame缓冲区
    avpicture_fill(reinterpret_cast<AVPicture *>(rgb_frame), out_butter, AV_PIX_FMT_RGBA,
                   avCodecContext->width, avCodecContext->height);
    // 原生绘制，需要ANativeWindow
    ANativeWindow *aNativeWindow = ANativeWindow_fromSurface(env, surface);
    if (aNativeWindow == nullptr) {
        return;
    }
    SwsContext *swsContext = sws_getContext(avCodecContext->width,
                                            avCodecContext->height, avCodecContext->pix_fmt,
                                            avCodecContext->width, avCodecContext->height,
                                            AV_PIX_FMT_RGBA, SWS_BICUBIC, NULL, NULL, NULL);
    //视频缓冲区
    ANativeWindow_Buffer nativeWindowBuffer;
    //解码
    int frameCount;
    uint8_t *dst;
    uint8_t *src;
    int destStride;
    int srcStride;
    while (av_read_frame(avFormatContext, avPacket) >= 0) {
        if (avPacket->stream_index == video_index) {
            avcodec_decode_video2(avCodecContext, avFrame, &frameCount, avPacket);
            //当解码一帧成功后，转换为rgb格式并绘制
            if (frameCount) {
                ANativeWindow_setBuffersGeometry(aNativeWindow, avCodecContext->width,
                                                 avCodecContext->height, WINDOW_FORMAT_RGBA_8888);
                // 上锁
                ANativeWindow_lock(aNativeWindow, &nativeWindowBuffer, NULL);
                // 转换为rgb格式
                sws_scale(swsContext, avFrame->data, avFrame->linesize, 0, avFrame->height,
                          rgb_frame->data, rgb_frame->linesize);
                dst = static_cast<uint8_t *>(nativeWindowBuffer.bits);
                destStride = nativeWindowBuffer.stride * 4;
                src = rgb_frame->data[0];
                srcStride = rgb_frame->linesize[0];
                for (int i = 0; i < avCodecContext->height; ++i) {
                    memcpy(dst + i * destStride, src + i * srcStride, srcStride);
                }
                ANativeWindow_unlockAndPost(aNativeWindow);
            }
        }
        av_free_packet(avPacket);
    }
    ANativeWindow_release(aNativeWindow);
    av_frame_free(&avFrame);
    av_frame_free(&rgb_frame);
    avcodec_close(avCodecContext);
    avformat_free_context(avFormatContext);

    env->ReleaseStringUTFChars(url, videoUrl);
}
}



