package com.alun;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CacheServiceImpl implements CacheService {

    private static AtomicInteger atomic = new AtomicInteger(0);

    /**
     * 自动加载缓存
     */
    public LoadingCache<String, Object> loadingCache;

    /**
     * guava线程池,用来产生ListenableFuture
     */
    private static ListeningExecutorService service = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(2,
            5, 5, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10)));

    @PostConstruct
    public void resh() {
        // 方式一
        loadingCache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .refreshAfterWrite(30, TimeUnit.SECONDS)
                .build(new CacheLoader<String, Object>() {
                    //默认的数据加载实现,当调用get取值的时候,如果key没有对应的值,就调用这个方法进行加载.
                    @Override
                    public Object load(String key) throws Exception {
                        return loadVal(key);
                    }

                    @Override
                    public ListenableFuture<Object> reload(String key, Object oldValue) {
                        ListenableFuture<Object> listenableFuture = service.submit(() -> {
                            Object value = loadVal(key);
                            return value;
                        });
                        Futures.addCallback(listenableFuture, new FutureCallback<Object>() {
                            @Override
                            public void onSuccess(Object result) {
                                System.out.println("刷新缓存成功");
                            }

                            @Override
                            public void onFailure(Throwable e) {
                                System.out.println("CommonCache刷新缓存异常");
                            }
                        });
                        return listenableFuture;
                    }
                });
    }


    public Object loadVal(String key) {
        System.out.println(Thread.currentThread().getName() + "刷新线程");
        if (atomic.addAndGet(1) % 2 == 0) {
            return "bb";
        }
        return "aa";
    }

    @Override
    public Object test(String key) throws ExecutionException {
        return loadingCache.get(key);
    }

    public Object test01(String key) throws ExecutionException {
        return loadingCache.get(key);
    }


}
