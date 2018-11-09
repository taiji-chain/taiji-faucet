package io.taiji.faucet;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.networknt.server.StartupHookProvider;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FaucetStartupHook implements StartupHookProvider {
    // cache the snapshot query result.
    public static Cache<String, Map<String, Long>> addresses;
    // cache the requests to enforce rate limit.
    public static Cache<String, Boolean> requests;

    @Override
    public void onStartup() {
        // the entry will be kept in cache if it is accessed within 24 hours
        addresses = Caffeine.newBuilder()
                .expireAfterAccess(24, TimeUnit.HOURS)
                .build();

        // the entry will be remove after 24 hours since it is written.
        requests = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build();
    }

}
