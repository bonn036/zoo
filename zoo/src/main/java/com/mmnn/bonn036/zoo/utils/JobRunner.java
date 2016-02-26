
package com.mmnn.bonn036.zoo.utils;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.Hashtable;

public class JobRunner {
    public final static Handler sHandler;
    public final static HandlerThread sThread;
    public final static Hashtable<Runnable, Runnable> sJobMap = new
            Hashtable<Runnable, Runnable>();

    static {
        sThread = new HandlerThread("JobRunner");
        sThread.start();
        sHandler = new Handler(sThread.getLooper());
    }

    public static void postJob(final Runnable job) {
        if (job != null) {
            Runnable runner = new Runnable() {
                @Override
                public void run() {
                    try {
                        job.run();
                        sJobMap.remove(job);
                    } catch (Throwable e) {
                    }
                }
            };
            sJobMap.put(job, runner);
            sHandler.post(runner);
        }
    }

    public static void removeJob(Runnable job) {
        if (job != null) {
            Runnable runner = sJobMap.remove(job);
            if (runner != null) {
                sHandler.removeCallbacks(runner);
            }
        }
    }
}
