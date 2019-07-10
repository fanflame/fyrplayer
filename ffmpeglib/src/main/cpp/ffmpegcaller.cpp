//
// Created by fanyiran on 2019-07-5.
//

#include <jni.h>
#include <string>

extern "C" {
#include <libavutil/opt.h>
#include <libavutil/frame.h>
#include <android/native_window_jni.h>
#include <libavutil/avutil.h>
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
#include <android/log.h>
}
#define LOG_TAG "ffmpeg"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

const char *TAG = "ffmpeg";
jint i_frame_interval;
jint encoding_bit_rate;
jint frame_rate;
jstring path;
int width;
int height;

AVCodec *codec;
AVCodecContext *codec_context;
AVPacket *packet;
int ret;
AVFrame *frame;
int frame_count;
FILE *out_file;

const int STATUS_UNKNOW = 0;
const int STATUS_INIT = 1;
const int STATUS_START = 2;
const int STATUS_RELEASE = 3;
volatile int status = STATUS_UNKNOW;

void get_config(JNIEnv *env, jobject config);

void encode(AVCodecContext *pContext, AVFrame *pFrame, AVPacket *pPacket, FILE *out_file);

void rotateY(jbyte *y);

extern "C" JNIEXPORT jint JNICALL
Java_com_fanyiran_fyrrecorder_recorder_ffmpeg_FFmpegImpl_nativeInit(JNIEnv *env, jobject thiz,
                                                                    jobject config) {
    get_config(env, config);

    codec = avcodec_find_encoder(AV_CODEC_ID_MPEG4);
    if (!codec)
        return -1;

    codec_context = avcodec_alloc_context3(codec);
    if (!codec_context)
        return -2;
    packet = av_packet_alloc();
    if (!packet) {
        return -3;
    }
    codec_context->bit_rate = encoding_bit_rate;
    codec_context->width = width;
    codec_context->height = height;
    codec_context->time_base = (AVRational) {1, 25};
    codec_context->framerate = (AVRational) {25, 1};
    codec_context->gop_size = 10;
    codec_context->max_b_frames = 1;
    codec_context->pix_fmt = AV_PIX_FMT_YUV420P;
    if (codec->id == AV_CODEC_ID_H264) {
        av_opt_set(codec_context->priv_data, "preset", "slow", 0);
    }
    ret = avcodec_open2(codec_context, codec, NULL);
    if (ret < 0) {
        LOGI("codec open failed :%s", av_err2str(ret));
        return -4;
    }
    const char *file_path = env->GetStringUTFChars(path, 0);
    out_file = fopen(file_path, "wb");
    if (!out_file) {
        return -7;
    }
    frame = av_frame_alloc();
    if (!frame) {
        return -5;
    }
    frame->format = codec_context->pix_fmt;
    frame->height = codec_context->height;
    frame->width = codec_context->width;
    ret = av_frame_get_buffer(frame, 0);
    if (ret < 0) {
        LOGI("av_frame_get_buffer :%s", av_err2str(ret));
        return -6;
    }
    status = STATUS_INIT;
    return 0;
}

void get_config(JNIEnv *env, jobject config) {
    jclass config_class = (env)->GetObjectClass(config);
    jfieldID jfieldId = env->GetFieldID(config_class, "iFrameInterval", "I");
    i_frame_interval = env->GetIntField(config, jfieldId);

    jfieldId = env->GetFieldID(config_class, "encodingBitRate", "I");
    encoding_bit_rate = env->GetIntField(config, jfieldId);

    jfieldId = env->GetFieldID(config_class, "frameRate", "I");
    frame_rate = env->GetIntField(config, jfieldId);

    jfieldId = env->GetFieldID(config_class, "outputFile", "Ljava/io/File;");
    jobject out_put_file = env->GetObjectField(config, jfieldId);
    jclass out_put_file_class = env->GetObjectClass(out_put_file);
    jmethodID file_path_method_id = env->GetMethodID(out_put_file_class, "getAbsolutePath",
                                                     "()Ljava/lang/String;");
    path = static_cast<jstring>(env->CallObjectMethod(out_put_file, file_path_method_id));

    jfieldId = env->GetFieldID(config_class, "videSize", "Landroid/util/Size;");
    jobject video_size_object = env->GetObjectField(config, jfieldId);
    jclass video_size_class = env->GetObjectClass(video_size_object);
    jmethodID video_size_width = env->GetMethodID(video_size_class, "getWidth", "()I");
    width = env->CallIntMethod(video_size_object, video_size_width);
    jmethodID video_size_height = env->GetMethodID(video_size_class, "getHeight", "()I");
    height = env->CallIntMethod(video_size_object, video_size_height);
}


extern "C" JNIEXPORT void JNICALL
Java_com_fanyiran_fyrrecorder_recorder_ffmpeg_FFmpegImpl_nativeStartRecord(JNIEnv *env,
                                                                           jobject thiz) {
    status = STATUS_START;
}

void encode(AVCodecContext *pContext, AVFrame *pFrame, AVPacket *pPacket, FILE *file) {
    if (pFrame) {
        LOGI("send frame %lli", pFrame->pts);
    }
    ret = avcodec_send_frame(pContext, pFrame);
    if (ret < 0) {
        LOGI("avcodec_send_frame :%s", av_err2str(ret));
        return;
    }
    while (ret >= 0) {
        ret = avcodec_receive_packet(pContext, pPacket);
        if (ret == AVERROR(EAGAIN) || ret == AVERROR_EOF) {
            LOGI("avcodec_receive_packet :%s", av_err2str(ret));
            return;
        } else if (ret < 0) {
            LOGI("error during encoding");
            return;
        }
        fwrite(pPacket->data, 1, packet->size, out_file);
        LOGI("fwirte something size:%d", packet->size);
        av_packet_unref(pPacket);
    }
}

uint8_t* rotateY(uint8_t *y) {
    int length = width * height;
    uint8_t y_temp[length];
    memset(y_temp,0,length);
    int index = 0;
    for (int w = 0; w < width; ++w) {
        for (int h = height - 1; h >= 0; --h) {
            y_temp[index] = y[w + h*width];
            index++;
        }
    }
    return y_temp;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_fanyiran_fyrrecorder_recorder_ffmpeg_FFmpegImpl_encode(JNIEnv *env, jobject instance,
                                                                jbyteArray dataY_,
                                                                jbyteArray dataU_,
                                                                jbyteArray dataV_) {
    if (status != STATUS_START) {
        return;
    }
    jbyte *data_y = env->GetByteArrayElements(dataY_, NULL);
    jbyte *data_u = env->GetByteArrayElements(dataU_, NULL);
    jbyte *data_v = env->GetByteArrayElements(dataV_, NULL);

    ret = av_frame_make_writable(frame);
    if (ret < 0) {
        LOGI("av_frame_make_writable :%s", av_err2str(ret));
    }
//    int x, y;
//    uint8_t *y = reinterpret_cast<uint8_t *>(data_y);
//    int length = width * height;
//    uint8_t y_temp[length];
//    memset(y_temp,0,length);
//    int index = 0;
//    for (int w = 0; w < width; ++w) {
//        for (int h = height - 1; h >= 0; --h) {
//            y_temp[index] = y[w + h*width];
//            index++;
//        }
//    }
//    frame->data[0] = y_temp;


    frame->data[0] = reinterpret_cast<uint8_t *>(data_y);
    frame->data[1] = reinterpret_cast<uint8_t *>(data_u);
    frame->data[2] = reinterpret_cast<uint8_t *>(data_v);

//    for (y = 0; y < codec_context->height; y++) {
//        for (x = 0; x < codec_context->width; x++) {
//            frame->data[0][y * frame->linesize[0] + x] = static_cast<uint8_t>(x + y +
//                                                                              frame_count * 3);
//        }
//    }
//    for (int y = 0; y < codec_context->height / 2; ++y) {
//        for (int x = 0; x < codec_context->width / 2; ++x) {
//            frame->data[1][y * frame->linesize[1] + x] = static_cast<uint8_t>(128 + y +
//                                                                              frame_count * 2);
//            frame->data[2][y * frame->linesize[2] + x] = static_cast<uint8_t>(64 + y +
//                                                                              frame_count * 2);
//
//        }
//    }

    frame->pts = frame_count;
    encode(codec_context, frame, packet, out_file);
    frame_count++;

    env->ReleaseByteArrayElements(dataY_, data_y, 0);
    env->ReleaseByteArrayElements(dataU_, data_u, 0);
    env->ReleaseByteArrayElements(dataV_, data_v, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_com_fanyiran_fyrrecorder_recorder_ffmpeg_FFmpegImpl_nativeStopRecord(JNIEnv *env,
                                                                          jobject thiz) {

}

extern "C" JNIEXPORT void JNICALL
Java_com_fanyiran_fyrrecorder_recorder_ffmpeg_FFmpegImpl_nativePause(JNIEnv *env, jobject thiz) {

}

extern "C" JNIEXPORT void JNICALL
Java_com_fanyiran_fyrrecorder_recorder_ffmpeg_FFmpegImpl_nativeResume(JNIEnv *env, jobject thiz) {

}

extern "C" JNIEXPORT void JNICALL
Java_com_fanyiran_fyrrecorder_recorder_ffmpeg_FFmpegImpl_nativeRelease(JNIEnv *env, jobject thiz) {
    status = STATUS_RELEASE;
    encode(codec_context, NULL, packet, out_file);
    uint8_t endcode[] = {0, 0, 1, 0xb7};
    fwrite(endcode, 1, sizeof(endcode), out_file);
    fclose(out_file);

    avcodec_free_context(&codec_context);
    av_frame_free(&frame);
    av_packet_free(&packet);
}
