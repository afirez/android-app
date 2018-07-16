package com.afirez.log;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.afirez.common.lifecycle.AppLifecycleCallbacks;

import timber.log.Timber;

public class LogAppLifeCycleCallbacks implements AppLifecycleCallbacks {
    @Override
    public void attachBaseContext(Application app, Context base) {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    @Override
    public void onCreate(Application application) {
    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            FakeCrashLibrary.log(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    FakeCrashLibrary.logError(t);
                } else if (priority == Log.WARN) {
                    FakeCrashLibrary.logWarning(t);
                }
            }
        }
    }

    /** Not a real crash reporting library! */
    public static final class FakeCrashLibrary {
        public static void log(int priority, String tag, String message) {
            // TODO add log entry to circular buffer.
        }

        public static void logWarning(Throwable t) {
            // TODO report non-fatal warning.
        }

        public static void logError(Throwable t) {
            // TODO report non-fatal error.
        }

        private FakeCrashLibrary() {
            throw new AssertionError("No instances.");
        }
    }

    @Override
    public void onTerminate(Application application) {

    }
}
