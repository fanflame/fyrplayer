### OpenGLES 入门2

当色彩被绘制到帧缓存时，存在一种称作合并（merging）的处理，这种处理决定了刚从片段着色器输出的“新”色彩如何与帧缓存中可能已经存在“旧”色彩混合在一起。当z-缓存化（z-buffering）被启用，一个检测被应用来查看刚被碎片着色器处理的几何点，相比于帧缓存中用于设置已经存在色彩的点，更靠近还是更远离观察者。z-缓存化在生成3D场景的图像时非常有用



模板测试是根据又一个缓冲来进行的，它叫做模板缓冲(Stencil Buffer)

一个模板缓冲中，（通常）每个模板值(Stencil Value)是8位的。所以每个像素/片段一共能有256种不同的模板值。我们可以将这些模板值设置为我们想要的值，然后当某一个片段有某一个模板值的时候，我们就可以选择丢弃或是保留这个片段了。

模板缓冲首先会被清除为0，之后在模板缓冲中使用1填充了一个空心矩形。场景中的片段将会只在片段的模板值为1的时候会被渲染（其它的都被丢弃了）。



这里需要注意的一点是,如果一个 attribute 在 shader 中被定义了,但是没有被使用,则在计算 shader 中 attribute 数量的时候,它不会被计算上,因为它会被 shader 优化掉。

VS = VertexShader
PS = PixelShader = FragmentShader
那么假如在 VS 中定义一个 varying 变量,那么 VS 中运算的每一个点都会包含一个 varying 变量对应的值,而 VS 只会针对 OpenGL ES 制定的几个点做运算,假如 OpenGL ES 只是要画一个三角形,那么经过 VS,就会得到这个三角形三个点的顶点坐标值。而如果将三个点的颜色通过 attribute 传入 VS, 那么 VS 就知道这三个点的颜色值,然后再通过 varying 把这三个点颜色传入 PS, 那么在传递的过程中,光珊化的时候,会根据原本那三个顶点包含的颜色值,进行插值,赋值给产生的新点。然后在 PS 中,会对所有产生的点进行运算,而针对每个点进行运算的时候,每个点都会有一个 varying 值,而这个 varying 值都是经过光珊化产生的新值。



void glShaderSource(GLuint shader, GLsizei count, const GLchar **string, const GLint *length);
二个参数和第三个参数的意思是:string 是一个由 count 个指针元素组成的数组,每个数组元素都是一个无终结的字符串。那么也就是说,可以把一个完整的 GLSL 书写的 shader source 写在一个字符串中,那么 count 就为 1,string 就是一个只有一个指针元素的数组。也可以把一个完整的 GLSL 书写的 shader source 写在多个字符串中,那么 count 就不是 1,string 就是由多个指针元素组成的数组。其实由于 GLSL 书写的 shader source 有很多格式化的内容,比如之前在说 GLSL 语法的时候就说到,fragment shader 中需要指明 float 的默认 precision,比如: precision lowp float; 。像这种内容基本每个 fragment shader 都需要写一遍,那么就可以把它拿出来,当在一个程序中需要创建多个 shader 的时候,就可以在传 shader source 的时候,将每个 shader 的 shader source 都分成 2 个或者 2 个以上 的字符串,其中一个字符串就是用于保存这些格式化的内容,这样就不需要在代码中多次创建和书写冗余的字符串了。


void glDeleteProgram(GLuint program);

如果 program 不是任何 GL Context 的当前 program, 也就是没有被任何 GL Context use,则它会被立即删除。否则的话,该 program 会被做一个标记,当这个 program 不再被任何 GL Context use 的时候,该 program 的删除操作才会被执行。



如果一个 shader 已经被 attach 到 program 上了,然后删除该 shader,那么不会立即删除该 shader,而是对该 shader 做一个标记,当这个 shader 从 program 上 detach, 且确保该 shader 没有 attach 到其他 program 上之后,删除操作才会被执行



GL_STATIC_DRAW 意思就是程序暗示这个 buffer object 只会被赋值一次,然后在 GPU 中可能会多次读取调用。除了 GL_STATIC_DRAW 之外,还有两种 usage,分别是 GL_DYNAMIC_DRAW,意思就是程序暗示这个 buffer object 可能会被赋值多次,且在 GPU 中可能会多次读取调用。 以及 GL_STREAM_DRAW,意思就是程序暗示这个 buffer object 只会被赋值一次, 然后在 GPU 中也只会被读取调用少量的几次



usage 的用法只适用于 performance 的暗示,通过传入这个暗示,GPU driver 可以更好的选择存储 buffer object 的方 式和位置,更好的优化 buffer object 的读写效率

过这个 API 也可以覆盖 buffer object 的全部数据,只需要将 offset 设置为 0,size 设置为 GL_BUFFER_SIZE,data 指向一组新的数据即可。 如果我们想更新 buffer object 中的全部内容的时候,理论上也可以直接通过再调用一次 glBufferData 进行重新赋值。但是建议使用 glBufferSubData 来操作这样的事情。因为 glBufferData 会牵扯到内存的重新分配,这样也会比较耗费资源,而 glBufferSubData 则不会牵扯到内存的重新分配。


我们在EGL中，通过eglCreateWindowSurface的时候，创建了一块绘制buffer，这块buffer的大小为屏幕分辨率的大小。然而虽然这块buffer的大小已经确定了，但是并不代表我们使用这块buffer进行绘制的时候，一定要完全按照这个buffer的大小去绘制。就好比我们有一块A4的画纸，那么我们想画一个太阳和一间房子，我们会分两次去进行绘制，第一次会在画纸的左上角先绘制太阳，第二次会在画纸的右下角绘制一间房子。那么第一次绘制的时候，我们只要把绘制区域设定在左上角即可，而第二次绘制，即把绘制区域设定在右下角。而glViewPort这个API，就是用于设定绘制区域的。



void glClearColor(GLclampf red, GLclampf green, GLclampf blue, GLclampf alpha);

glClear，用于清理绘制buffer。因为我们通过egl创建的绘制buffer，其实也就是一块内存，但是这个内存在刚被创建好的时候，并不会被初始化，那么根据平台不同，里面保存的东西可能不同，就好比我们虽然准备了一张画纸，但是这个画纸上面可能原本就有东西，这些东西是不确定的，所以我们先要把这张画图涂上底色，而我们可能希望这个画纸的底色是白色，也有可能希望是黑色的，或者其他颜色，而glClearColor这个API，就是设定一种颜色，作为清理绘制buffer所使用的颜色。



void glClear(GLbitfield mask);

这是我们将要学习的第一个绘制API，用于使用预先设定的值去清理绘制buffer，比如我们想要清理绘制buffer的颜色，那么通过刚才的glclearcolor API，我们已经确定好一种颜色了，然后通过这个API，就可以调用GPU，对绘制buffer的颜色根据刚才设定的颜色进行清理。



有很多别的GL状态会影响glclear的结果，比如scissor test，scissor的意思就是在绘制区域中再设定一个小的绘制区域，假如屏幕分辨率为1080,720，viewport为(0,0)到(1080, 720)，然后当OpenGL ES开启了scissor之后，再通过glScissor设定小的绘制区域为(0,0)到(1,1)，那么再执行glClear，就不会clear一整块绘制buffer，而是只会clear那一小块绘制区域。再比如glColorMask,glColorMask是限定了绘制buffer中的那些颜色分量可以被写，默认是四个颜色分量都可以写，但是也可以限定只能r通道可写，然后假如glClearColor设定为（1,1,1,1），也就是准备clear成白色，但是由于只有r通道可写，那么其实是会被clear成红色。



void glColorMask(GLboolean red, GLboolean green, GLboolean blue, GLboolean alpha);

当PS结束后，Mask可以限制color、depth、stencil是否可以写入对应的buffer。比如这个glColorMask API就是用于控制color buffer R\G\B\A通道的写入权限的。

这个函数一共有4个输入参数，分别是rgba四个值，用于确定RGBA四个通道是否有写入权限，如果传入的为GL_TRUE，则说明该通道有写入权限。初始状态下，默认RGBA四个通道都是可写的。当传入GL_FALSE的时候，所有的color buffers的R通道将都不可写




缩小包体的一个重要方向，就是缩小纹理对应的原始图片文件的大小。所以这些原始图片通常压缩为PNG、JPG或者TGA格式，以占用更少的磁盘空间。而基本不会使用BMP这种无压缩的格式。

而想要生成纹理，第一步要做的就是读取这些压缩的原始图片，在CPU端将它们解压，然后由于传入GPU的时候需要用到OpenGL ES的API




在CPU端，这组数据是按照行的顺序进行存放的，比如先存放了第一行第一列那个像素的信息，假如占据了1个byte，然后存放第一行第二列那个像素的信息，依次类推，那么假如原始图片的宽度为15，每个像素占据1个byte，那么在CPU端一行也就只有15byte。然后我们从这组数据中取数据去生成纹理的时候，也要按照这个顺序一一读取，然后把读取到的数据去GPU中构建一张纹理图片。那么问题就来了。由于在CPU中数据也是按照组存放的，有几行就有几组，按照刚才我们的假设在CPU端一行只有15个byte，假如我们读取的时候假如按照8对齐，那么先读了8个，再读8个，那么就读取了16byte的信息，也就出现了读越界的情况。所以，一定要设定好对齐规则，这个对齐规则可以理解成一次性读取CPU几个数据。那么在这里我们可以看到，我们会根据图片的宽度和CPU中每个像素占的位数相乘得到CPU中每行的bit，然后除以8，得到CPU中每行占据多少个byte。然后看看其能被多少整除。如果能被8整除，那么我们每次可以从CPU读取8byte的数据；当然也可能只能被1整除，比如图片宽度为15，每个像素占的位数为3byte，那么每行所占据的位数就是45byte，就只能被1整除。当然对齐的字节数越高，系统越能对其优化。所以使用纹理尽量使用POT，也就是power of two，2的幂次方作为宽度。

所以不管如何，我们在这里一定要设置好一个合理的对齐规则。而这个对其规则，是通过OpenGL ES API来设置的，glPixelStorei。

void glPixelStorei(GLenum pname, GLint param);

原则上说这个API是用于设置像素的存储模式了，其实我们可以理解为这个API是用于设置我们读写像素的对齐规则。
从CPU读取数据的时候要遵守刚才glPixelStorei设置的对齐规则。


这个函数的第一个输入参数只能是GL_UNPACK_ALIGNMENT或者GL_PACK_ALIGNMENT。将客户端的颜色数据传输至GL服务端的过程称为解包unpack。相反，将服务器像素读取到客户端的过程叫做打包pack。我们刚才说的就是GL_UNPACK_ALIGNMENT，也就是将数据从CPU端解包出来的时候的对齐准则。而GL_PACK_ALIGNMENT则是将数据从GPU端读取出来的对齐准则



第二个输入参数为一个整形数据用于指定参数的新的值。默认为4，可以设置的值为1、2、4、8。如果使用了其他值，那么就会出现GL_INVALID_VALUE的error。

而第一层mipmap就是纹理的原始尺寸，而第二层mipmap的尺寸为原始宽高各除以2，依次类推，最后一层mipmap的尺寸为宽高均为1。所以如果level超过了log2（max）



假如图像的宽或者高不是2的幂，那么有个专业术语叫做NPOT，non power of two。在OpenGL ES2.0中NPOT的texture是不支持mipmap的，所以针对NPOT的texture，如果level大于0，也就会出现GL_INVALID_VALUE的错误



GL_LUMINANCE指的是每个像素点只有一个luminance值，相当于RGB的值全为luminance的值，alpha为1。GL_LUMINANCE_ALPHA指的是每个像素点有一个luminance值和一个alpha值，相当于RGB的值全为luminance的值，alpha值保持不变



type指的每个通道的位数以及按照什么方式保存，到时候读取数据的时候是以byte还是以short来进行读取。只能是GL_UNSIGNED_BYTE, GL_UNSIGNED_SHORT_5_6_5, GL_UNSIGNED_SHORT_4_4_4_4, and GL_UNSIGNED_SHORT_5_5_5_1。当type为GL_UNSIGNED_BYTE的时候，每一个byte都保存的是一个颜色通道中的值，当type为GL_UNSIGNED_SHORT_5_6_5, GL_UNSIGNED_SHORT_4_4_4_4, and GL_UNSIGNED_SHORT_5_5_5_1的时候，每个short值中将包含了一个像素点的所有颜色信息，也就是包含了所有的颜色通道的值。从CPU往GPU传输数据生成纹理的时候，会将这些格式的信息转成float值，方法是比如byte，那么就把值除以255，比如GL_UNSIGNED_SHORT_5_6_5，就把red和blue值除以31，green值除以63，然后再全部clamp到闭区间[0,1]，设计这种type使得绿色更加精确，是因为人类的视觉系统对绿色更敏感。而type为GL_UNSIGNED_SHORT_5_5_5_1使得只有1位存储透明信息，使得每个像素要么透明要么不透明，这种格式比较适合字体，这样可以使得颜色通道有更高的精度。


void glTexSubImage2D(GLenum target, GLint level, GLint xoffset, GLint yoffset, GLsizei width, GLsizei height, GLenum format, GLenum type, const GLvoid * data);

这个API的功能和刚才的glTexImage2D类似，顾名思义，刚才那个API是给texture object传入数据，这个glTexSubImage2D是给texture object的一部分传入数据。这个命令不会改变texture object的internalformat、width、height、参数以及指定部分之外的内容。

种压缩纹理，在CPU端需要使用特殊的原图片，也就是比如PVR或者ETC格式的图片，这些格式的图片保存着生成压缩纹理的信息，这些文件的大小要小于JPG、PNG等图片，而且在传输给GPU的时候，不需要通过glTexImage2D这个API，而是通过压缩纹理的专用API glCompressedTexImage2D，通过这种API使得图片信息不需要经过解压缩，直接可以传给GPU，然后把信息保持压缩状态保存在GPU中，这种方法不仅能减小资源的大小，还可以节省CPU往GPU传输数据的带宽，以及减少GPU的内存占用。
虽然刚传入GPU的时候不用解压缩，但是使用的时候还是需要解压缩的




过 EGL 生成一块该格式的绘制 buffer。生成绘制 buffer 的过程,其实是我们通过 API 生成一块 surface,surface 是一个抽象概念,但是这个 surface 包含了绘制 buffer,假如我们所选择的格式是支持 RGBA、depth、stencil 的。那 么 surface 对应的绘制 buffer 有一块 Color buffer,到时候会用于保存图片的颜色信息,保存的方式在上一节我们做过介绍,就是 buffer 会对应上百万个像素点, 每个像素点有自己的颜色值,将这些颜色值按照像素点的顺序保存在 color buffer 中,就形成了一张完整的 color buffer。绘制 buffer 还有一块 depth buffer,depth buffer 也按照同样的方法,按照顺序保存了所有像素点的 depth 值;以及一块 stencil buffer,同理可知,stencil buffer 也是按照顺序保存了所有像素点的 stencil 值




一个 thread 同一时间只能启动有相同格式的 一块 surface 和一块对应于 OpenGL ES 的 context,一块 context 同时也只能被一 个 thread 启动。


EGLSurface eglCreateWindowSurface(EGLDisplay dpy, EGLConfig config, EGLNativeWindowType win, const EGLint *attrib_list);

第四个参数,拿一个属性进行解释,比如 EGL_RENDER_BUFFER,它就定义了绘制 API 绘制的时候应该会绘制到哪个 buffer 中,可以绘制到 single buffer,也可以绘制到 back buffer, 我们已经知道了 windowsurface 创建的是 backbuffer,那么如果绘制到 single buffer,则相当于直接绘制到屏幕上,如果绘制到 back buffer,那么就会先绘制到 back buffer,再通过 eglswapbuffer 转移到屏幕上。


个 native window handle 只能创建一个 rendering surface,而一个 display 可以创建多个 rendering surface。context 与 native window 无关,也就是 display 可以创建多个 context,每个 context 对应一种绘制 API,只要 surface 和 context 的格式匹配,两者就可以进行关联,但是同一时间,一个 surface 只能 和一个 context 进行关联,一个 thread 中,一种绘制 API 也只能有一个 context。



Opengl ES中存在两种渲染目标

屏幕渲染目标

离屏渲染目标

在Api层同一时间只能有一个渲染目标处于激活状态；当前渲染目标通过glBindFrameBuffer来绑定，当绑定id为0的目标时会切回window的渲染目标（默认FBO）

On-screen Render Targets
On-screen render targets are tightly defined by EGL. The rendering activity for one frame has very clearly defined demarcation of what is one frame and what is the next; all rendering to FBO 0 between two calls to eglSwapBuffers() defines the rendering for one frame.

On-screen 渲染目标

On-screen渲染目标是通过EGL定义。

In addition the color, depth, and stencil buffers in use are defined when the context is created, and their configuration is immutable. By default the value of the color, depth, and stencil immediately after eglSwapBuffers() is undefined - the old value is not preserved from the previous frame - allowing the GPU driver to make guaranteed assumptions about the use of the buffers. In particular we know that depth and stencil are only transient working data, and we never need to write them back to memory.

Off-screen Render Targets
Off-screen render targets are less tightly defined.

Firstly, there is no equivalent of eglSwapBuffers() which tells the driver that the application has finished rendering to an FBO and it can be submitted for rendering; the flush of the rendering work is inferred from other API activities. We'll look more about the inferences the Mali drivers support in the next section.

Secondly, there are no guarantees about what the application will do with the buffers attached to the color, depth, and stencil attachment points. An application may use any of these as textures, or reattach them to a different FBO, for example reloading the depth value from a previous render target as the starting depth value for a different render target. By default the behavior of OpenGL ES is to preserve all attachments, unless explicitly discarded by the application via a call to glInvalidateFramebuffer(). Note: this is a new entry point in OpenGL ES 3.0; in OpenGL ES 2.0 you can access the equivalent functionality via the glDiscardFramebufferExt() extension entry point which all Mali drivers support.