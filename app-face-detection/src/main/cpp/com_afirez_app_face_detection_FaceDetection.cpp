//
// Created by afirez on 25/8/2018.
//
#include <jni.h>
#include <opencv2/opencv.hpp>
#include <android/bitmap.h>
#include <android/log.h>

#define LOG_TAG "FaceDetection"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

using namespace cv;

CascadeClassifier classifier;

extern "C"
void bitmap2Mat(JNIEnv *env, jobject bitmap, Mat &dst);

extern "C"
void mat2Bitmap(JNIEnv *env, Mat src, jobject bitmap);

extern "C"
JNIEXPORT jint JNICALL
Java_com_afirez_app_face_detection_FaceDetection_detect(
        JNIEnv *env,
        jobject instance,
        jobject bitmap) {

    Mat mat;
    LOGI("[1] bitmap2Mat: ");
    bitmap2Mat(env, bitmap, mat);

    LOGI("[2] gray: ");
    Mat grayMat;
    cvtColor(mat, grayMat, COLOR_BGRA2GRAY);

    LOGI("[3] equalizeHist: ");
    equalizeHist(grayMat, grayMat);

    LOGI("[4] face detection");
    std::vector<Rect> faces;
    classifier.detectMultiScale(grayMat, faces, 1.1, 2, 0 | CV_HAAR_SCALE_IMAGE, Size(30, 30));
    LOGI("   face detected: has %d face", faces.size());
    if (faces.size() == 1) {
        Rect face = faces[0];
        LOGI("    draw face rectangle: [ x = %d, y = %d, width = %d , height = %d ]", face.x,
             face.y, face.width, face.height);
        rectangle(mat, face, Scalar(0, 255, 0));
        rectangle(mat,
                  Point(face.x, face.y),
                  Point(face.x + face.width, face.y + face.height),
                  Scalar(0, 255, 0));
        LOGI("[5] mat2Bitmap: ");
//        mat2Bitmap(env, mat, bitmap);

        Mat saveMat = Mat(grayMat, face);
        // mat 保存成文件  png ,上传到服务器吧，接着下一张（眨眼，张嘴巴）
        imwrite("/sdcard/face.png", grayMat);
        mat2Bitmap(env, mat, bitmap);
    }

    return faces.size();
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_afirez_app_face_detection_FaceDetection_loadCascade(
        JNIEnv *env,
        jobject instance,
        jstring filePath_) {
    const char *filePath = env->GetStringUTFChars(filePath_, 0);

    classifier.load(filePath);
    LOGI("Classifier file loaded");

    env->ReleaseStringUTFChars(filePath_, filePath);
    return 0;
}

void bitmap2Mat(JNIEnv *env, jobject bitmap, Mat &dst) {
    AndroidBitmapInfo bitmapInfo;
    AndroidBitmap_getInfo(env, bitmap, &bitmapInfo);

    void *pixels;
    AndroidBitmap_lockPixels(env, bitmap, &pixels);

    dst.create(bitmapInfo.width, bitmapInfo.height, CV_8UC4);

    if (bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGI("bitmap2Mat: RGBA_8888 -> CV_8UC4");
        Mat src(bitmapInfo.width, bitmapInfo.height, CV_8UC4, pixels);
        src.copyTo(dst);
    } else if (bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGB_565) {
        LOGI("bitmap2Mat: RGB_565 -> CV_8UC4");
        Mat src(bitmapInfo.width, bitmapInfo.height, CV_8UC2, pixels);
        cvtColor(src, dst, COLOR_BGR5652BGRA);
    } else {
        LOGI("bitmap2Mat: missed");
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}


void mat2Bitmap(JNIEnv *env, Mat src, jobject bitmap) {
    AndroidBitmapInfo bitmapInfo;
    AndroidBitmap_getInfo(env, bitmap, &bitmapInfo);

    void *pixels;
    AndroidBitmap_lockPixels(env, bitmap, &pixels);

    if (bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        Mat dst(bitmapInfo.width, bitmapInfo.height, CV_8UC4, pixels);
        if (src.type() == CV_8UC4) {
            LOGI("mat2Bitmap: CV_8UC4 -> CV_8UC4");
            src.copyTo(dst);
        } else if (src.type() == CV_8UC2) {
            LOGI("mat2Bitmap: CV_8UC2 -> CV_8UC4");
            cvtColor(src, dst, COLOR_BGR5652BGRA);
        } else if (src.type() == CV_8UC1) {
            LOGI("mat2Bitmap: CV_8UC1 -> CV_8UC4");
            cvtColor(src, dst, COLOR_GRAY2BGRA);
        }
    } else if (bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGB_565) {
        Mat dst(bitmapInfo.width, bitmapInfo.height, CV_8UC2, pixels);
        if (src.type() == CV_8UC4) {
            LOGI("mat2Bitmap: CV_8UC4 -> CV_8UC2");
            cvtColor(src, dst, COLOR_BGRA2BGR565);
        } else if (src.type() == CV_8UC2) {
            LOGI("mat2Bitmap: CV_8UC2 -> CV_8UC2");
            src.copyTo(dst);
        } else if (src.type() == CV_8UC1) {
            LOGI("mat2Bitmap: CV_8UC1 -> CV_8UC2");
            cvtColor(src, dst, COLOR_GRAY2BGR565);
        }
    } else {
        LOGI("mat2Bitmap: missed");
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}
