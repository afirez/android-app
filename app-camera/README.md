# Android Camera 相机开发

## 在 Android Manifest.xml 中声明相机权限

```
<uses-permission android:name="android.permission.CAMERA" />
```

可能还需要用到以下两个权限：

```
<uses-feature android:name="android.hardware.camera" android:required="true"/>
<uses-feature android:name="android.hardware.camera.autofocus" android:required="false"/>

```

required 属性是说明这个特性是否必须满足。比方说示例的设置就是要求必须拥有相机设备但可以没有自动对焦功能.

这两个声明是可选的，它们用于应用商店（Google Play）过滤不支持相机和不支持自动对焦的设备。

另外在保存照片时需要写入储存器的权限，也需要加上读写储存器的权限声明：

```
<uses-permission android:name="android.permission.WEITE_EXTERNAL_STORAGE" />
```

## 打开相机设备

市面上销售的手机／平板等消费产品基本标配两个摄像头, 更有前置或后置双摄像头．开发双摄像头 App 该是种怎样的体验．

> 在打开相机设备前，先获取有多少个相机设备．如果你的开发需求里包含切换前后摄像头功能，可以获取摄像头数量来判断是否存在后置摄像头．

```
int cameras = Camera.getNumberOfCameras();
```

> 这个 API 返回值为摄像头的数量：非负整数。对应地，摄像头序号为: cameras - 1。
> 例如在拥有前后摄像头的手机设备上，其返回结果是2，则第一个摄像头的 cameraId 是 0，通常对应手机背后那个大摄像头；第二个摄像头的 cameraId 是 1，通常对应着手机的前置自拍摄像头.

相机是硬件设备资源，在使用设备资源前需要将其打开，可通过 API Camera.open(int cameraId) 来打开． 参考以下代码：

```
public static Camera openCamera(int cameraId) {
    try{
        return Camera.open(cameraId);
    }catch(Exception e) {
        return null;
    }
}

```

在打开相机设备后，你将获得一个Camera对象，并独占相机设备资源。

> 注意: 打开相机设备可能会失败，你一定要检查打开操作是否成功。
> 打开失败的可能原因有两种：
> - 安装 App 的设备上根本没有摄像头，例如某些平板或特殊 Android 设备.
> - cameraId 对应的摄像头正被使用，可能某个 App 正在后台使用它录制视频。

## 配置相机参数

通过以下 API 可以获取当前相机设备的默认配置参数．

```
Camera.getParameters()
```

下面列出部分参数：

- 闪光灯模式参数配置

可以通过以下 API 获取当前相机的闪光灯配置参数：

```
Parameters.getFlashMode()
```

  - Camera.Parameter.FLASH_MODE_AUTO 自动模式, 当光线较暗时自动打开闪光灯；
  - Camera.Parameters.FLASH_MODE_OFF 关闭闪光灯；
  - Camera.Parameters.FLASH_MODE_ON 拍照时闪光灯；
  - Camera.Parameters.FLASH_MODE_RED_EYE 闪光灯参数，防红眼模式

- 对焦模式配置参数

可以通过以下 API 获取:

```
Parameters.getFocusMode()
```

  - Camera.Parameters.FOCUS_MODE_AUTO 自动对焦模式，摄影小白专用模式；
  - Camera.Parameters.FOCUS_MODE_FIXED 固定焦距模式，拍摄老司机模式；
  - Camera.Parameters.FOCUS_MODE_EDOF 景深模式，文艺女青年最喜欢的模式；
  - Camera.Parameters.FOCUS_MODE_INFINITY 远景模式，拍风景大场面的模式；
  - Camera.Parameters.FOCUS_MODE_MACRO 微焦模式，拍摄小花小草小蚂蚁专用模式；

- 场景模式配置参数

可以通过以下 API 获取：

```
Parameters.getSceneMode()
```

  - Camera.Parameters.SCENE_MODE_BARCODE 扫描条码场景；
  - Camera.Parameters.SCENE_MODE_ACTION 动作场景，就是抓拍跑得飞快的运动员、汽车等场景用的；
  - Camera.Parameters.SCENE_MODE_AUTO 自动选择场景；
  - Camera.Parameters.SCENE_MODE_HDR 高动态对比度场景，通常用于拍摄晚霞等明暗分明的照片；
  - Camera.Parameters.SCENE_MODE_NIGHT 夜间场景；

Camera API 提供了非常多的参数接口供开发者设置，可以翻阅相关API文档。

## 设置相机预览方向

相机预览图需要设置正确的预览方向才能正常地显示预览画面，否则预览画面会被挤压得很惨。

在通常情况下，如果我们需要知道设备的屏幕方向，可以通过以下 API 来获取:

```
Resources.Configuration.orientation
```

Android 屏幕方向有“竖屏”和“横屏”两种，对应的值分别是:
- ORIENTATION_PORTRAIT ;
- ORIENTATION_LANDSCAPE .

另外，设置预览方向的 API 为:

```
Camera.setDisplayOrientation(int degrees)
```

> 这里就要注意了，参数 degrees 以角度为单位，　而且只能是 [０,90,180,270] 其中之一，默认为 0 ,
> 是指手机的左侧为摄像头顶部画面。记得只能是 [0,90,180,270] 其中之一，输入其它角度数值会报错。

如果你想让相机跟随设备的方向，预览界面顶部一直保持正上方，以下代码供参考：

```
public static void followScreenOrientation(Context context, Camera camera){
    final int orientation = context.getResources().getConfiguration().orientation;
    if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
        camera.setDisplayOrientation(180);
    }else if(orientation == Configuration.ORIENTATION_PORTRAIT) {
        camera.setDisplayOrientation(90);
    }
}

```

## 预览 View

可以用作预览的 View 有:

- SurfaceView；
- TextureView.

一般使用 SurfaceView 作为相机预览 View . 在 SurfaceView　中获得　SurfaceHolder ,
并通过　以下　API 设置预览：

```
Camera.setPreviewDisplay(SurfaceHolder holder) throws IOException
```

> 一定要记得以下几点：
>
> - 调用 startPreview() 方法启动预览，否则预览View不会显示任何内容；
> - 拍照操作需要在 startPreview() 方法执行之后调用；
> - 每次拍照后，预览 View 会停止预览。所以连续拍照，需要重新调用startPreview()来恢复预览；

Camera 在 setPreviewDisplay 时接受 SurfaceHolder , 可以通过　SurfaceHolder.CallBack 获得．
我们可以通过继承 SurfaceView 来实现相机预览效果．参考以下代码：

```
public class PreviewSurfaceView extends SurfaceView
        implements SurfaceHolder.Callback {

    private static final String TAG = "PreviewSurfaceView";

    private final SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    public PreviewSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated -> startPreview");
        CameraUtils.startPreview(mCamera, holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mSurfaceHolder.getSurface() == null) {
            Log.d(TAG, "surfaceChanged -> restartPreview failed (surface == null)");
            return;
        }
        Log.d(TAG, "surfaceChanged -> restartPreview success");
        CameraUtils.followScreenOrientation(getContext(), mCamera);
        CameraUtils.stopPreview(mCamera);
        CameraUtils.startPreview(mCamera, mSurfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed -> stopPreview");
        CameraUtils.stopPreview(mCamera);
    }

    public void setCamera(Camera camera) {
        if (camera == null) {
            Log.d(TAG, "setCamera (camera == null)");
        }
        mCamera = camera;
    }
}
```

> 从代码中可以看出相机预览时的核心代码是 SurfaceHolder.Callback 的回调:
> **在创建 / 销毁时启动 / 停止预览动作。**
> CameraUtils 见附加工具代码

在 PreviewSurfaceView 类中，我们利用了 SurfaceHolder.Callback 的生命周期回调来实现自动管理预览生命周期控制：

- 当 SurfaceView 被创建后开启预览；
- 当 SurfaceView 被销毁时关闭预览；
- 当 SurfaceView 尺寸被改变时重置预览；

＞ 预览 View　需要注意预览输出画面的尺寸．相机输出画面只支持部分尺寸． 这个后面再讨论．

## 拍照
在启用预览View后，就可以通过以下 API 拍摄一张照片:

```
Camera.takePicture(
    ShutterCallback shutter,
    PictureCallback raw,
    PictureCallback jpeg)
```

> - 第一个，ShutterCallback接口，在拍摄瞬间瞬间被回调，通常用于播放“咔嚓”这样的音效；
> - 第二个，PictureCallback接口，返回未经压缩的RAW类型照片；
> - 第三个，PictureCallback接口，返回经过压缩的JPEG类型照片；

在实际项目中，可根据需求，再通过 PictureCallback 获得照片. 例如项目中需要 Bitmap，
就可以通过 BitmapFactory 很方便地将 byte 数组转化为 Bitmap.

```
public abstract class BitmapCallback implements Camera.PictureCallback {
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        onPictureTaken(BitmapFactory.decodeByteArray(data, 0, data.length));
    }

    public abstract void onPictureTaken(Bitmap bitmap);
}
```

##　释放相机设备

> - 在打开一个相机设备后，意味着你的 App 就独占了这个设备，其它 App 将无法使用它。
> - 因此在你不需要相机设备时，记得调用 release() 方法释放设备，
> - 再使用时可以重新打开，这并不需要多大的成本。
> - 可以选择在 stopPreview() 后即释放相机设备。

## 附加工具性代码

### 判断手机设备是否有相机设备

```
public static boolean hasCameraDevice(Context context) {
    return context.getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_CAMERA);
}
```

### 判断是否支持自动对焦

```
public static boolean isAutoFocusSupported(Camera.Parameters params) {
   List<String> modes = params.getSupportedFocusModes();
   return modes.contains(Camera.Parameters.FOCUS_MODE_AUTO);
}
```

### CameraUtils.java

```
public class CameraUtils {

    private static final String TAG = "CameraUtils";

    public static Camera open(int cameraId) {
        try {
            return Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void followScreenOrientation(Context context, Camera camera){
        if (context == null || camera == null) {
            return;
        }
        final int orientation = context.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            camera.setDisplayOrientation(180);
        }else if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            camera.setDisplayOrientation(90);
        }
    }

    public static void startPreview(Camera camera, SurfaceHolder holder){
        if (camera == null || holder == null) {
            return;
        }
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "Error while START preview for camera", e);
        }
    }

    public static void stopPreview(Camera camera){
        if (camera == null) {
            return;
        }
        try {
            camera.stopPreview();
            camera.setPreviewDisplay(null);
        } catch (Exception e){
            Log.e(TAG, "Error while STOP preview for camera", e);
        }
    }

    public static boolean hasCameraDevice(Context context) {
        return context.getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static boolean isAutoFocusSupported(Camera.Parameters params) {
        List<String> modes = params.getSupportedFocusModes();
        return modes.contains(Camera.Parameters.FOCUS_MODE_AUTO);
    }
}
```

## 后续问题

### Camera 视频拍摄功能开发

### Camera2 的相关开发