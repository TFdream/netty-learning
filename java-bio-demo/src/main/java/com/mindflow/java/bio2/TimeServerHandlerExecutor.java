package com.mindflow.java.bio2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimeServerHandlerExecutor {

    private ExecutorService executor;

    public TimeServerHandlerExecutor(int maxPoolSize, int queueSize) {
        executor = new ThreadPoolExecutor(Runtime.getRuntime()
		.availableProcessors(), maxPoolSize, 120L, TimeUnit.SECONDS,
		new ArrayBlockingQueue(queueSize));
    }
    public void execute(java.lang.Runnable task) {
	executor.execute(task);
    }
}