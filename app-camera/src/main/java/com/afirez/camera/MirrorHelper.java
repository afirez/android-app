package com.afirez.camera;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by lenovo on 2018/3/15.
 */

public class MirrorHelper {

    public MirrorHelper(SurfaceView surfaceView) {
        proxySurfaceHolderFor(surfaceView);
    }

    public void proxySurfaceHolderFor(SurfaceView surfaceView) {
        // 获取 surfaceView中的 surfaceHolder
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        // 创建代理接口的实现
        mInvocationHandler = new LockCanvasInvocationHandler(surfaceHolder);
        SurfaceHolder newSurfaceHolder = (SurfaceHolder) Proxy.newProxyInstance(
                surfaceHolder.getClass().getClassLoader(),
                surfaceHolder.getClass().getInterfaces(),
                mInvocationHandler
        );
        try {
            Field fieldHolder = SurfaceView.class.getDeclaredField("mSurfaceHolder");
            fieldHolder.setAccessible(true);
            fieldHolder.set(surfaceView, newSurfaceHolder);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void apply() {
        mInvocationHandler.mirror();
    }

    private LockCanvasInvocationHandler mInvocationHandler;

    public static class LockCanvasInvocationHandler implements InvocationHandler {
        Object mObject;

        public LockCanvasInvocationHandler(Object object) {
            mObject = object;
        }

        private boolean mirror;

        public void mirror() {
            mirror = !mirror;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("lockCanvas".equals(method.getName())) {
                Canvas canvas = (Canvas) method.invoke(mObject, args);
                if (mirror) {
                    canvas.scale(-1, 1, canvas.getWidth() / 2, canvas.getHeight() / 2);
                }
                return canvas;
            }
            return method.invoke(mObject, args);
        }
    }
}
