package edu.oswego.csc375;

import java.util.concurrent.CountDownLatch;

public class ValueLatch<T> {
    private T val = null;
    private final CountDownLatch latch = new CountDownLatch(1);

    public boolean isSet(){
        return latch.getCount() == 0;
    }

    public synchronized void setVal(T val){
        if (! isSet()){
            this.val = val;
            latch.countDown();
        }
    }

    public T getVal() throws InterruptedException {
        latch.await();
        synchronized(this){
            return val;
        }
    }

}
