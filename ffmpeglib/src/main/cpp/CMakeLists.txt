# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)
add_library(ffmpegrender # 生成的动态库名称
        SHARED # 库的类型
        ffmpegcaller.cpp# 库的源码相对本文件的路径
        )

add_library(
        ffmpeg # 引入的静态库库名称
        SHARED  # 设置库引入方式
        IMPORTED # 引入
)
set_target_properties(ffmpeg PROPERTIES IMPORTED_LOCATION
        ${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${CMAKE_ANDROID_ARCH_ABI}/libffmpeg.so # 设置静态库路径，必须是绝对路径
        )
# 与ffmpegshare_3.3.7不同的是是用了include_directories
include_directories(ffmpegrender PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/include) # 设置.h文件位置

find_library(log-lib
        log)

# 链接库
target_link_libraries(
        ffmpegrender
        ffmpeg
        android
        ${log-lib}
)