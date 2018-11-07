package io.taiji.faucet;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.networknt.server.StartupHookProvider;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FaucetStartupHook implements StartupHookProvider {
    public static Cache<String, Map<String, Long>> addresses;
    @Override
    public void onStartup() {
        addresses = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build();
        System.out.println("addresses is " + addresses);
    }
}
