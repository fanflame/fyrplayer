### OpenGLES 入门

### GPU vs CPU

![cpu vs gpu 2](/Users/momo/Documents/opengl入门/imgs/cpu vs gpu 2.jpg)

![cpu vs gpu](/Users/momo/Documents/opengl入门/imgs/cpu vs gpu.jpg)

运算器(ALU)、控制器(包含程序计数器，指令寄存器)、DRAM（Dynamic RAM，[动态随机存取存储器]）

从GPU和CPU的结构对比可以看出虽然GPU单核运算没有CPU的功能强大，但是在数量上运算单元GPU要远超CPU.

GPU成百上千的核心可以同时处理上千个线程。GPU将复杂的问题分解为成千上万的单独的任务，比如在图像渲染过程中当纹理，光照，形状渲染必须一次性解决的时候，就体现出GPU的优势。

## OpenGL

Open Graphics Library

是用于2D，3D图像***渲染***的跨语言、跨平台的***规范***。OpenGL规范由Khronos Group维护 

包含一系列可以操作图形、图像的函数

规范严格规定每个函数如何执行，以及它们的输出值，具体实现由各个显卡厂商实现

早期OpenGL使用固定渲染管线，缺少灵活性。固定渲染管线模式中OpenGL的大多数功能都被库隐藏起来，开发者很少能控制OpenGL如何进行计算的自由

OpenGL3.2开始废弃固定渲染模式，鼓励使用核心模式，这个版本之后拥有更高的灵活性以及效率，但是学习成本也随之上升。

![固定管线渲染](/Users/momo/Documents/opengl入门/imgs/固定管线渲染.jpg)

​																			图为固定管线渲染结果

![可编程管道渲染](/Users/momo/Documents/opengl入门/imgs/可编程管道渲染.jpg)

​																		图为可编程管线渲染结果

以上两图为固定管线和可编程管线渲染效果对比，从中可以看出，可编程管线在光照，阴影等细节有很大提升。

#### 状态机

OpenGL是基于状态的。比如

```glsl
//vao相关设置...
...
GLES30.glBindVertexArray(vaoArray[0])
GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0)
```

以上代码片段，Opengl当前绑定了vaoArray[0]，当调用Opengl的glDrawElements方法绘制三角形时，绘制的顶点信息，不如坐标，颜色等，都是在vaoArray[0]之前绑定的缓存中取。

OpenGL的状态被称为OpenGL context(OpenGL 上下文)，OpenGL提供改变状态的接口比如设置选项，操作缓冲等，最后使用OpenGL上下文来渲染。

同一个OpenGL上下文不能同时在多个线程中同时运行。虽然可以在多个线程中有多个OpenGL context，但是不能在两个线程中同时操作同一个context。

## OpenGLES
  OpenGLES是OpenGL子集，适用于嵌入式以及手机等低功耗设备，OpenGLES 2.0开始支持可编程管线。
## GLSL

OpenGL Shading language，是一种类c的语言，语法与c语言相近，是三大主流Shader Language之一：HLSL(基于Direct3D)、GLSL、CG（支持OpenGL、Direct3D）.GLSL是为图形计算量身定制的，它包含一些针对向量和矩阵操作的有用特性

OpenGL着色器是用GLSL写成的
着色器(Shader)是运行在GPU上的小程序。这些小程序为图形渲染管线的某个特定部分而运行.

shader在GPU中进行编译.

##### GLSL基本语法

每个着色器的入口点都是main函数
GLSL中包含C等其它语言大部分的默认基础数据类型：int、float、double、uint和bool。GLSL也有两种容器类型在shader中经常使用分别是向量(Vector)和矩阵(Matrix)

GLSL中的向量是一个可以包含有1、2、3或者4个分量的容器，分量的类型可以是前面默认基础类型的任意一个。它们可以是下面的形式（n代表分量的数量）：
vecn	包含n个float分量的默认向量
bvecn	包含n个bool分量的向量
ivecn	包含n个int分量的向量
uvecn	包含n个unsigned int分量的向量
dvecn	包含n个double分量的向量

在 GLSL 中定义这种 vec 的变量类型,是因为在 Shader 中存在大量这种多 component 的变量进行各种操作的运算,而如果直接在 GPU 中运行这种 vector 级别的运算,比一个一个进行单值运算要快的多。所以为了提高效率,在 shader 中定义了这种 vec2、3、4 的变量类型,然后将这些变量直接保存到 GPU 中对应硬件上,通过 GPU 的对应模块,一次运算可以得到之前 2 次 3 次 4 次或者更多次运算的结果,这样从带宽、运算效率和功耗上,都会得到大大的优化,所以定义这种变量非常有必要。

mat 的成员变量可以通过[]来访问,如果一个 matrix 变量名后面跟一个[],[] 中写入一个 int 常量,比如 mat4 a,获取 a[0],那么就是说把这个 mat 按列分成几个 vector,a[0]就是 a 这个矩阵第一列的 vector,

如果将一个标量传入 vector 的构造函数中,那么生成的这个 vector 中所有的值都是这个标量值。比如 vec3(float)。如果将一个标量传入 matrix 的 构造函数中,那么生成的这个 matrix 中对角线上的所有的值都是这个标量值, 其余的将都是 0.

如果 shader 中存放判断语句,就会对 GPU 造成比较大的负荷,不同 GPU 的实现方式不同,多数 GPU 会对判断语句的两种情况都进行运算,然后根据判断结果取其中一个。

Vertex Shader示例：

```glsl
layout(location = 0) attribute vec3 vertexCoord
void main(){
	gl_Position = vec4(vertexCoord,1.0);
}
```

## EGL

OpenGL ES 通过 GPU 计算得到一张图片,这张图 片在内存中其实就是一块 buffer,存储有每个点的颜色信息等。而这张图片最终是要显示到屏幕上,所以还需要具体的窗口系统来操作,OpenGL ES 并没有相关的函数。所以OpenGL ES需要EGL。

EGL全称:Embedded Graphic Interface,是 OpenGL ES 和底层 Native 平台视窗系统之间的接口
EGL使用大概流程:首先,通过 EGL 获取到手机屏幕的handle,获取到手机支持的配置(RGBA8888/RGB565 之类,表示每个像素中包 含的颜色等信息的存储空间是多少位),然后根据这个配置创建一块包含默认 buffer 的surface(buffer 的大小是根据屏幕分辨率乘以每个像素信息所占大小计 算而得)和用于存放 OpenGL ES 状态集的 context,并将它们 enable 起来。然后, 通过 OpenGL ES 操作 GPU 进行计算,将计算的结果保存在 surface 的 buffer 中。 最后,使用 EGL将绘制的图片显示到手机屏幕上。

EGL 生成的context 也是一块 buffer。 OpenGL ES 是状态机,那么在绘制中会牵扯到各种各样的状态,这些状态全部都有默认值,可以通过 OpenGL ES 对这些状态进行改变。这些状态值就会保存在 context 中。比如 OpenGL ES 所用到的混合模式、纹理图片、program 还有各种 BO 等信息。

EGL创建context示例：

```java
display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
if (display == EGL14.EGL_NO_DISPLAY) {
    LogUtil.v(TAG, "eglGetDisplay failed!");
    return -1;
}
int[] version = new int[2];
boolean result = EGL14.eglInitialize(display, version, 0, version, 1);
if (!result) {
    LogUtil.v(TAG, "initialize failed!");
    return -1;
}
int[] num_config = new int[1];
int[] attrib_list = {
    EGL14.EGL_RED_SIZE, 8,
    EGL14.EGL_GREEN_SIZE, 8,
    EGL14.EGL_BLUE_SIZE, 8,
    EGL14.EGL_ALPHA_SIZE, 8,
    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
    EGL_RECORDABLE_ANDROID, 1,// placeholder for recordable [@-3]
    EGL14.EGL_NONE
    };
configs = new EGLConfig[1];
result = EGL14.eglChooseConfig(display, attrib_list, 0, configs, 0, configs.length, num_config, 0);
if (!result) {
  LogUtil.v(TAG, "eglGetConfigs failed!");
  return -1;
}
int[] attrList = {
    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
    EGL14.EGL_NONE
};
eglContext = EGL14.eglCreateContext(display, configs[0], EGL14.EGL_NO_CONTEXT, attrList, 0);
if (eglContext == EGL14.EGL_NO_CONTEXT) {
    LogUtil.v(TAG, "eglCreateContext failed!");
    return -1;
}
```



### 渲染管线

在OpenGL中，所有对象都在3D空间中，而屏幕是2D像素数组，所以OpenGL的大部分工作都是把3D坐标转变为适应屏幕的2D像素。3D坐标转为2D坐标的处理过程是由OpenGL的图形渲染管线管理。图形渲染管线可以被划分为几个阶段，每个阶段将会把前一个阶段的输出作为输入。

通过 OpenGL ES 绘制图片的时候,我们需要通过 OpenGL ES API 创建用于在 GPU 上运行的 shader, 然后将通过 CPU 获取到的图片顶点信息传入 GPU 中的 Shader 中。在 Vertex Shader 中通过矩阵变换,将顶点坐标从模型坐标系转换到世界坐标系,再到观察坐标系,到裁剪坐标系,最后投影到屏幕坐标系中,计算出在屏幕上各个顶点的坐标。然后,通过光栅化得到所有像素点的信息,并在 Fragment shader 中计算出所有像素点的颜色。最后,通过 OpenGL ES 的 API 设定的状态,将得到的像素信息进行 depth/stencil test、blend,得到最终的图片。

![pipeline](/Users/momo/Documents/opengl入门/imgs/pipeline.jpeg)

#### 顶点着色器（vertex shader）

顶点着色器主要的目的是把3D坐标转为另一种3D坐标，同时顶点着色器允许我们对顶点属性进行一些基本处理。

Vertex shader 一次只能操作一个顶点。

![model](/Users/momo/Documents/opengl入门/imgs/model.jpeg)

在OpenGL中表达几何体为三角形的集合。一方面，三角形足够简单可以被OpenGL非常高效地处理，而另一方面，借助很多三角形的集合，我们可以近似出复杂形状的表面。划分的三角形越多，渲染出的图形越逼真，当然也越消耗性能。

##### 坐标系统

一个顶点在被转化为片段之前需要经历的坐标空间

- 本地空间(Local Space，或者称为物体空间(Object Space))

- 世界空间(World Space)

- 观察空间(View Space，或者称为视觉空间(Eye Space))

- 裁剪空间(Clip Space)

- 屏幕空间(Screen Space)

  为了将坐标从一个坐标系变换到另一个坐标系，需要用到几个变换矩阵（变换矩阵：它能通过对物体进行位移、缩放、旋转来将它置于它本应该在的位置或朝向）：模型(Model)矩阵、观察矩阵(View)、投影(Projection)三个矩阵。顶点坐标起始于本地空间(Local Space)，之后变为世界坐标(World Coordinate)，观察坐标(View Coordinate)，裁剪坐标(Clip Coordinate)，最终转化为屏幕坐标。

  ![坐标系统](/Users/momo/Documents/opengl入门/imgs/坐标系统.jpg)



1. 本地坐标：比如在建模软件中创建了一个立方体。立方体的原点位于(0, 0, 0)，立方体的8个顶点相对于（0，0，0）都有自己的坐标。
2. 世界坐标：用于组织独立的物体形成一个完整的场景。世界坐标中每个对象的顶点坐标相对于世界原点又有了新的坐标。从本地变换到世界空间；该变换是由模型矩阵(Model Matrix)实现的。
3. 观察空间是将世界空间坐标转化为用户视野前方的坐标而产生的结果。从世界坐标到观察坐标：该变换是由观察矩阵(View Matrix)实现
4. 相机在某个固定坐标观察到场景是有限的，在视野范围之外的物体需要裁减掉（不需要渲染），并且将坐标映射至-1.0到1.0的范围内。从观察空间到裁剪坐标：该变换是由投影矩阵。(View Matrix)实现。
5. 最后，使用一个叫做视口变换(Viewport Transform)的过程将裁剪坐标变换为屏幕坐标。视口变换将位于-1.0到1.0范围的坐标变换到由glViewport函数所定义的坐标范围内。最后变换出来的坐标将会送到光栅器，将其转化为片段

将顶点变换到各个不同的坐标系原因：有些操作在特定的坐标系统中才有意义且更方便。例如，当需要对物体进行修改的时候，在本地空间中来操作会更说得通；如果要对一个物体做出一个相对于其它物体位置的操作时，在世界坐标系中来做这个才更说得通。

#### 图元装配（Shape Assembly）

图元装配阶段将顶点着色器输出的所有顶点作为输入将所有的点装配成指定图元的形状（opengl中只定义了绘制点，线，三角形的方法）。

#### 几何着色器(Geometry shader)

图元装配阶段的输出会传递给几何着色器(Geometry Shader)。几何着色器把图元形式的一系列顶点的集合作为输入，它可以通过产生新顶点构造出新的（或是其它的）图元来生成其他形状。

#### 光栅化(Rasterization)

在被光栅化之前需要确定正面或背面，即剔除操作。OpenGL中默认以逆时针顶点顺序为正面。

OpenGL接下来的工作是在屏幕上绘制每个三角形。这个步骤被称作光栅化（rasterization）。对于每个三角形，它使用3个顶点位置把三角形放置在屏幕上。光栅化器（rasterizer）指出哪些像素位于三角形之内同时从顶点到这些像素做线性插值（比如颜色）。

光栅化确定的像素生成供片段着色器(Fragment Shader)使用的片段(Fragment)。在片段着色器运行之前会执行裁切(Clipping)。裁切会丢弃超出视图以外的所有像素，用来提升执行效率。

#### 片段着色器（Fragment shader）

一个片段是渲染一个像素所需的所有数据。

片段着色器的主要目的是计算一个像素的最终颜色。通常，片段着色器包含3D场景的数据（比如光照、阴影、光的颜色等等），这些数据可以被用来计算最终像素的颜色。

#### 测试与混合（Test and Blending）

在所有对应颜色值确定以后，最终的对象将会被传到Alpha测试和混合(Blending)阶段。在这个阶段检测片段的对应的深度（和模板(Stencil)）值，用它们来判断这个像素是其它物体的前面还是后面，决定是否应该丢弃。这个阶段也会检查alpha值（物体的透明度）并对物体进行混合(Blend)。所以，即使在片段着色器中计算出来了一个像素输出的颜色，在渲染多个三角形的时候最后的像素颜色也可能完全不同。

##### 深度测试

OpenGL存储所有深度信息于Z缓冲区（Z-Buffer）中，即深度缓冲区（Depth Buffer）。片段着色器中，内建gl_FragCoord向量的z值包含了那个特定片段的深度值。当绘制开始的时候，会通过glClear这个API，传入GL_DEPTH_BUFFER_BIT，将depth buffer中的值统一为glClearDepthf设定的值。通过VS和光栅化计算好顶点3D坐标后，将Z值与depth buffer中的值进行Test，Test方法由glDepthFunc确定，如果失败，则丢弃该像素，如果通过，则算是通过了Depth Test，它是由OpenGL自动完成的。下一步再进行Stencil Test。

##### Alpha Test

在片段着色器中判断alpha值，如果alpha小于阈值，则discard



最后梳理一下：EGL创建OpenGL上下文；绘制时输入顶点信息，将顶点数据传入GLSL编写的Shader中，渲染管线通过矩阵变换将输入的3d坐标变换为平面上的2d像素坐标，通过剔除面、光栅化，在片段着色器中确定像素颜色，通过深度、Alpha测试确定最后图像，

 最后,使用 EGL将绘制的图片显示到手机屏幕上。