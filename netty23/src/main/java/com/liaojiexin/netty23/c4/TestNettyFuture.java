package com.liaojiexin.netty23.c4;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;


/**
 * @ClassName TestNettyFuture
 * @Description TODO
 * @Author liao
 * @Date 5:18 下午 2023/1/17
 **/
@Slf4j
public class TestNettyFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EventLoopGroup group=new NioEventLoopGroup();
        EventLoop eventLoop = group.next();
        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算");
                Thread.sleep(1000);
                return 50;
            }
        });
        //同步获取结果
//        log.debug("等待结果");
//        log.debug("结果是 {}", future.get());  //get方法会阻塞住直到获取到结果

        //异步获取结果
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                //这里可以直接用getNow方法，因为get为阻塞方法，但是这个异步执行，是由同个线程处理，这个回调方法operationComplete即可拿到结果
                log.debug("接收结果:{}",future.getNow());
            }
        });
        /**异步执行结果如下，可以看到是为同个线程，都不是主线程
         * 17:30:46.954 [nioEventLoopGroup-2-1] DEBUG com.liaojiexin.netty23.c4.TestNettyFuture - 执行计算
         * 17:30:47.960 [nioEventLoopGroup-2-1] DEBUG com.liaojiexin.netty23.c4.TestNettyFuture - 接收结果:50
         */
    }
}
