package com.liaojiexin.netty23.c2;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName TestEventLoop
 * @Description TODO    演示eventloop的作用
 * @Author liao
 * @Date 12:52 下午 2023/1/16
 **/
@Slf4j
public class TestEventLoop {

    public static void main(String[] args) {
        //1.创建事件循环组,这里的EventLoopGroup有多种类型，下面举例两种常用的类型
        /**
         * NioEventLoopGroup既可以处理io事件，也可以处理普通任务，还可以处定时任务
         * 通过查看NioEventLoopGroup的源码可以发现NioEventLoopGroup()构造函数会创建
         * 一个线程数等于系统核心数*2的线程池，当然可以用NioEventLoopGroup(int nThreads)
         * 设置自己想要的线程数，下面我们设置2个线程数是为了方便演示效果
         */
        EventLoopGroup group=new NioEventLoopGroup(2);
        //DefaultEventLoop只能处理普通任务和定时任务
//        EventLoopGroup group=new DefaultEventLoop();

        //2.获取下一个事件循环对象,这里用next()方法来获取
        System.out.println(group.next());   //获取到第一个
        System.out.println(group.next());   //获取到第二个
        System.out.println(group.next());   //循环回来，又获取到第一个
        /**
         * 上面打印结果：可以发现第一个和第三个都为NioEventLoop@1d251891对象
         * io.netty.channel.nio.NioEventLoop@1d251891
         * io.netty.channel.nio.NioEventLoop@48140564
         * io.netty.channel.nio.NioEventLoop@1d251891
         */

        //3.执行普通方法，因为EventLoop继承了线程池，所以它有线程池的相关方法，例如submit、execute等
        //异步处理，这里逻辑和一些事件分发等等场景是差不多一样的
        group.next().submit(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("ok");
        });
        //主线程
        log.debug("main");
        /**
         * 上面的打印结果如下:先执行了主线程，然后1秒后执行了异步任务nioEventLoopGroup-2-1名称是netty自己起的
         * 15:55:09.823 [main] DEBUG com.liaojiexin.netty23.c2.TestEventLoop - main
         * 15:55:10.825 [nioEventLoopGroup-2-1] DEBUG com.liaojiexin.netty23.c2.TestEventLoop - ok
         */

        //4.执行定时任务,scheduleAtFixedRate()方法就是启动一个定时任务
        //scheduleAtFixedRate参数说明，第一个参数表示要执行的任务；第二个参数表示开始时间，0秒表示马上开始，1秒表示1秒后开始
        //第三个参数表示任务执行的间隔时间，第四个参数表示时间单位
        group.next().scheduleAtFixedRate(()->{
            log.debug("定时任务执行中。。");
        },0,1, TimeUnit.SECONDS);
        /**
         * 打印结果如下，可以看到任务马上执行，并且每隔1秒就执行1次打印
         * 16:03:55.344 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c2.TestEventLoop - 定时任务执行中。。
         * 16:03:56.344 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c2.TestEventLoop - 定时任务执行中。。
         * 16:03:57.341 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c2.TestEventLoop - 定时任务执行中。。
         */

    }
}
