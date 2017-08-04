package com.alibaba.dubbo.trace.sampler;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xiaobaxi on 2017/7/31.
 */
public class PercentageSampler implements Sampler {

    private AtomicLong count = new AtomicLong();
    private int levelOne = 100;
    private int levelTwo = 500;
    private Long lastTime = -1L;

    @Override
    public boolean isCollect() {
        boolean isSample = true;
        long n = count.incrementAndGet();
        if (System.currentTimeMillis() - lastTime  < 1000) {
            if (n > levelOne && n <= levelTwo) {
                n = n % 2;
                if (n != 0) {
                    isSample = false;
                }
            }
            if (n > levelOne) {
                n = n % 10;
                if (n != 0) {
                    isSample = false;
                }
            }
        } else {
            count.getAndSet(0);
            lastTime = System.currentTimeMillis();
        }
        return isSample;
    }

    public static void main(String[] args) {
        PercentageSampler sampler = new PercentageSampler();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; ++i) {
            System.out.println(sampler.isCollect());
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
