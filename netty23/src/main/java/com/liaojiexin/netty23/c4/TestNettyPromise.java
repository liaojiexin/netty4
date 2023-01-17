package com.liaojiexin.netty23.c4;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * @ClassName TestNettyPromise
 * @Description TODO
 * @Author liao
 * @Date 5:41 下午 2023/1/17
 **/
@Slf4j
public class TestNettyPromise {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1.准备EventLoop对象
        EventLoop eventLoop = new NioEventLoopGroup().next();
        //2.主动创建Promise对象，其实Promise是一个存储结果的容器
        DefaultPromise<Integer> promise=new DefaultPromise<>(eventLoop);

        new Thread(()->{
            //3.任意一个线程执行计算完毕后向Promise填充结果
            log.debug("开始计算");
            try{
                int i=1/0;
                Thread.sleep(1000);
                promise.setSuccess(80);
            }catch (Exception exception){
                exception.printStackTrace();
                promise.setFailure(exception);
            }
        }).start();

        //4.接收数据结果
        log.debug("等待结果。。。。");
        log.debug("结果是:{}",promise.get());  //这里也可以用addListener方法来进行异步处理

    }
}
