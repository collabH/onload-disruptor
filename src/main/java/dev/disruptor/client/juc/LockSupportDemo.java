package dev.disruptor.client.juc;

import org.junit.Test;

import java.util.concurrent.locks.LockSupport;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-09-28 20:10
 * @description LockSupport使用
 */
public class LockSupportDemo {
    /**
     * 使用Object锁
     */
    @Test
    public void objectLock() throws InterruptedException {
        Object lock = new Object();
        Thread a = new Thread(() -> {
            int sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += i;
            }
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(sum);
        });
        a.start();
        Thread.sleep(3000);
        //直接这样会报错，因为wait方法会释放锁
//        lock.notify();
        synchronized (lock) {
            lock.notify();
        }
    }

    /**
     * LockSupport方式
     *
     * @throws InterruptedException
     */
    @Test
    public void lockSupport() throws InterruptedException {
        Thread a = new Thread(() -> {
            int sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += i;
            }
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //后执行，unpark和park可以顺序调换
            LockSupport.park();
            System.out.println(sum);
        });
        a.start();
        Thread.sleep(1000);
        //先被执行
        LockSupport.unpark(a);
    }
}
