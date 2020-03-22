package com.nowcoder.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTest {

    public static  void main(String[] args){
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }

}
//生产者线程类
class Producer implements Runnable{
    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue){
       //得到当前对象的queue
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i=0;i<100;i++){
               //每20s生产一个
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName()+"生产:"+queue.size());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

//消费者者线程类
class Consumer implements Runnable{
    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue){
        //得到当前对象的queue
        this.queue = queue;
    }

    @Override
    public void run() {
        try {

            while (true){
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName()+"消费了"+queue.size());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}