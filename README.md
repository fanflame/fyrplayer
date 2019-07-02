包含播放器以及拍摄器

 
 
todo
- 处理声音
- 在线播放：mp4,hls,dash等
- 特效处理：滤镜，美颜等
- MediaCodec，MediaMuxer录制视频
- ffmpeg + x264录制
- openGL添加水印
- ffmpeg添加水印
- MediaCodec添加水印


attention:
- 使用MediaRecorder录制比例必须是内部支持的比例，而且像素大小有限制，太大不能拍摄
- createCaptureSession第一个参数surface出入个数有限制，在华为honor max8 最多支持4个
- 使用MediaRecorder录制得到的视频帧率，码率与设置的不同。why? 需要结合硬件平台设置？codec与muxer的呢需要特定分辨率？




MediaRecorderImpl
- 只支持在预览的时候添加水印，不支持视频录制的时候添加水印
