package com.alun;

import java.util.concurrent.ExecutionException;

public interface CacheService {
    Object test(String key) throws ExecutionException;
}
