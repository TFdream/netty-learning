package com.mindflow.netty4.rpc.core;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by Ricky on 2017/5/9.
 */
public class RpcFuture<V> {
    private static final CancellationException CANCELLED = new CancellationException();
    private volatile boolean sendRequestSuccess = true;
    private volatile boolean haveResult;
    private volatile V result;
    private volatile Throwable exc;
    private CountDownLatch latch;

    //处理开始时间
    private final long beginTimestamp = System.currentTimeMillis();
    /**超时时间**/
    private long timeout;
    private TimeUnit unit;

    //异步回调
    private InvokeCallback callback;

    public RpcFuture() {
    }

    public RpcFuture(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
    }

    public RpcFuture(long timeout, TimeUnit unit, InvokeCallback callback) {
        this.timeout = timeout;
        this.unit = unit;
        this.callback = callback;
    }

    public void execCallback(){
        if(isDone()){
            if(this.exc != null) {
                this.callback.onFailure(this.exc);
            } else {
                this.callback.onSuccess(this.result);
            }
        }
    }

    public boolean isAsync() {
        return this.callback!=null;
    }
    public boolean isCancelled() {
        return this.exc == CANCELLED;
    }

    public boolean isDone() {
        return this.haveResult;
    }

    public void setResult(V v) {
        synchronized(this) {
            if(!this.haveResult) {
                this.result = v;
                this.haveResult = true;
                if(this.latch != null) {
                    this.latch.countDown();
                }
            }
        }
    }

    public void setFailure(Throwable throwable) {
        if(!(throwable instanceof IOException) && !(throwable instanceof SecurityException)) {
            throwable = new IOException(throwable);
        }

        synchronized(this) {
            if(!this.haveResult) {
                this.exc = throwable;
                this.haveResult = true;
                if(this.latch != null) {
                    this.latch.countDown();
                }
            }
        }
    }

    public void setResult(V result, Throwable exc) {
        if(exc == null) {
            this.setResult(result);
        } else {
            this.setFailure(exc);
        }

    }

    public V get() throws InterruptedException, ExecutionException {
        if(!this.haveResult) {
            boolean wait = this.prepareForWait();
            if(wait) {
                this.latch.await();
            }
        }
        return returnResult();
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if(!this.haveResult) {
            boolean wait = this.prepareForWait();
            if(wait && !this.latch.await(timeout, unit)) {
                throw new TimeoutException();
            }
        }
        return returnResult();
    }

    private V returnResult() throws ExecutionException {
        if(this.exc != null) {
            if(this.exc == CANCELLED) {
                throw new CancellationException();
            } else {
                throw new ExecutionException(this.exc);
            }
        } else {
            return this.result;
        }
    }

    private boolean prepareForWait() {
        synchronized(this) {
            if(this.haveResult) {
                return false;
            } else {
                if(this.latch == null) {
                    this.latch = new CountDownLatch(1);
                }
                return true;
            }
        }
    }

    public boolean isSendRequestSuccess() {
        return sendRequestSuccess;
    }

    public void setSendRequestSuccess(boolean sendRequestSuccess) {
        this.sendRequestSuccess = sendRequestSuccess;
    }

    public long getBeginTimestamp() {
        return beginTimestamp;
    }

    public long getTimeoutMillis() {
        return unit.toMillis(timeout);
    }

}
