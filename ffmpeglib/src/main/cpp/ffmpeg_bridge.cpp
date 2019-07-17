//
// Created by fanyiran on 2019-07-5.
//

#include <jni.h>
#include <string>
#include "ffmpeg_status.h"
#include "ffmpeg_helper.h"
#include "ffmpeg_config.h"

extern "C" {
#include <android/log.h>
}
#define LOG_TAG "ffmpeg"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

const char *TAG = "ffmpegtag";
jint i_frame_interval;
jint encoding_bit_rate;
jint frame_rate;
jstring path;
int width;
int height;

FFmpegHelper *fFmpegHelper = NULL;

void get_config(JNIEnv *env, jobject config);

//uint8_t *rotateY(uint8_t *);
void log_callback(void*, int, const char*, va_list);

extern "C" JNIEXPORT jint JNICALL
Java_com_fanyiran_fyrrecorder_recorder_ffmpeg_FFmpegImpl_nativeInit(JNIEnv *env, jobject thiz,
                                                                    jobject config) {
    if (fFmpegHelper) {
        LOGI("ffmpeg has inited");
        return -1;
    }
    av_log_set_callback(log_callback);
    get_config(env, config);
    FFmpegConfig fFmpegConfig;
    fFmpegConfig.width = width;
    fFmpegConfig.height = height;
    fFmpegConfig.frame_rate = frame_rate;
    fFmpegConfig.encoding_bit_rate = encoding_bit_rate;
    fFmpegConfig.i_frame_interval = i_frame_interval;
    fFmpegConfig.file_name = env->GetStringUTFChars(path, NULL);
    fFmpegConfig.callback = log_callback;
    fFmpegHelper = new FFmpegHelper(fFmpegConfig);
//    FILE file;
    return 0;
}

void log_callback(void* param1, int param2, const char* param3, va_list param4){
    LOGI("%s",param3);
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
    if (!fFmpegHelper) {
        LOGI("ffmpeg is null");
        return;
    }
    fFmpegHelper->init();
}

//
//uint8_t *rotateY(uint8_t *y) {
//    int length = width * height;
//    uint8_t y_temp[length];
//    memset(y_temp, 0, length);
//    int index = 0;
//    for (int w = 0; w < width; ++w) {
//        for (int h = height - 1; h >= 0; --h) {
//            y_temp[index] = y[w + h * width];
//            index++;
//        }
//    }
//    return y_temp;
//}

extern "C"
JNIEXPORT void JNICALL
Java_com_fanyiran_fyrrecorder_recorder_ffmpeg_FFmpegImpl_encode(JNIEnv *env, jobject instance,
                                                                jbyteArray dataY_,
                                                                jbyteArray dataU_,
                                                                jbyteArray dataV_) {
    if (!fFmpegHelper || fFmpegHelper->getStatus() != STATUS_START) {
        LOGI("ffmpeg is null or status is wrong");
        return;
    }
    jbyte *data_y = env->GetByteArrayElements(dataY_, NULL);
    jbyte *data_u = env->GetByteArrayElements(dataU_, NULL);
    jbyte *data_v = env->GetByteArrayElements(dataV_, NULL);

    fFmpegHelper->encode(reinterpret_cast<uint8_t *>(data_y), reinterpret_cast<uint8_t *>(data_u),
                         reinterpret_cast<uint8_t *>(data_v));

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

    if (!fFmpegHelper) {
        return;
    }
    fFmpegHelper->release();
    delete (fFmpegHelper);
    fFmpegHelper = NULL;

}
