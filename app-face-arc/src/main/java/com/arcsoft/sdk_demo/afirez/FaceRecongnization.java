package com.arcsoft.sdk_demo.afirez;

import android.content.Context;

import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.arcsoft.sdk_demo.Application;
import com.arcsoft.sdk_demo.FaceDB;

import java.util.ArrayList;
import java.util.List;

public class FaceRecongnization {

    private volatile boolean hasAftEngine;
    private AFT_FSDKEngine aftEngine = new AFT_FSDKEngine();
    private AFT_FSDKVersion aftVersion = new AFT_FSDKVersion();

    private volatile boolean hasAfrEngine;
    private AFR_FSDKEngine afrEngine = new AFR_FSDKEngine();
    private AFR_FSDKVersion afrVersion = new AFR_FSDKVersion();

    private List<FaceDB.FaceRegist> registry;

    public FaceRecongnization(Context context) {
        registry = ((Application) context.getApplicationContext()).mFaceDB.mRegister;
    }

    public List<AFT_FSDKFace> detect(byte[] buffer, int width, int height) {
        AFT_FSDKError error;
        if (!hasAftEngine) {
            synchronized (this) {
                if (!hasAftEngine) {
                    error = aftEngine.AFT_FSDK_InitialFaceEngine(
                            FaceDB.appid,
                            FaceDB.ft_key,
                            AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT,
                            16, 5);
                    error = aftEngine.AFT_FSDK_GetVersion(aftVersion);
                    hasAftEngine = true;
                }
            }
        }

        ArrayList<AFT_FSDKFace> faces = new ArrayList<>();
        aftEngine.AFT_FSDK_FaceFeatureDetect(
                buffer,
                width,
                height,
                AFT_FSDKEngine.CP_PAF_NV21,
                faces
        );
        return faces;
    }

    public RecognizeResult recognize(byte[] buffer, int width, int height, AFT_FSDKFace face) {
        AFR_FSDKError error;
        if (!hasAfrEngine) {
            synchronized (this) {
                if (!hasAfrEngine) {
                    error = afrEngine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
                    error = afrEngine.AFR_FSDK_GetVersion(afrVersion);
                    hasAfrEngine = true;
                }
            }
        }

        AFR_FSDKFace faceId = new AFR_FSDKFace();
        afrEngine.AFR_FSDK_ExtractFRFeature(
                buffer,
                width,
                height,
                AFR_FSDKEngine.CP_PAF_NV21,
                face.getRect(),
                face.getDegree(),
                faceId
        );

        if (registry.isEmpty()) {
            return new RecognizeResult("", 0f, faceId);
        }

        float max = 0.0f;
        String name = null;
        AFR_FSDKMatching matching = new AFR_FSDKMatching();
        for (FaceDB.FaceRegist fr : registry) {
            for (AFR_FSDKFace faceId1 : fr.mFaceList) {
                afrEngine.AFR_FSDK_FacePairMatching(faceId, faceId1, matching);
                if (matching.getScore() > max) {
                    max = matching.getScore();
                    name = fr.mName;
                }
            }
        }

        return new RecognizeResult(name, max, faceId);
    }


    public static class RecognizeResult {
        public String name;
        public float score;
        public AFR_FSDKFace faceId;

        public RecognizeResult(String name, float score, AFR_FSDKFace faceId) {
            this.name = name;
            this.score = score;
            this.faceId = faceId;
        }
    }

    public void destroy() {
        if (hasAftEngine) {
            aftEngine.AFT_FSDK_UninitialFaceEngine();
            hasAftEngine = false;
        }

        if (hasAfrEngine) {
            afrEngine.AFR_FSDK_UninitialEngine();
            hasAfrEngine = false;
        }
    }
}
