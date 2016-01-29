package com.hujiang.restvolley;

import android.os.Handler;
import android.os.Looper;

/**
 * 实现多任务并发调度，高效，低性能
 * @version 1.0.0
 */
public class TaskScheduler {

    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static ThreadPool mThreadPool = new ThreadPool("com.hujiang.restvolley.task_scheduler");

    /**
     * 以轻量级的方式执行一个异步任务.
     * @param backgroundTaskRunnable 异步任务
     */
    public static void execute(final Runnable backgroundTaskRunnable) {
        if (backgroundTaskRunnable != null) {
            mThreadPool.addTask(backgroundTaskRunnable);
        }
    }

    /**
     * execute a task.
     * @param backgroundRunnable background task
     * @param foregroundRunnable foreground task
     */
    public static void execute(final Runnable backgroundRunnable, final Runnable foregroundRunnable) {
        mThreadPool.addTask(new Runnable() {
            @Override
            public void run() {
                if (backgroundRunnable != null) {
                    backgroundRunnable.run();
                }

                if (foregroundRunnable != null) {
                    mHandler.post(foregroundRunnable);
                }
            }
        });
    }

    /**
     * execute a task.
     * @param task {@link Task}
     * @param <IT> in paramter's type
     * @param <OT> out paramter's type
     */
    public static <IT, OT> void execute(final Task<IT, OT> task) {
        exec(task);
    }

    private static <IT, OT> void exec(final Task<IT, OT> task) {
        if (task != null) {
            mThreadPool.addTask(new Runnable() {
                @Override
                public void run() {
                    final OT out = task.onDoInBackground(task.mInput);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            task.onPostExecuteForeground(out);
                        }
                    });

                }
            });
        }
    }
}
