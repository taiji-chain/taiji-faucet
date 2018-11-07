package io.taiji.faucet;

import com.networknt.server.ShutdownHookProvider;

public class FaucetShutdownHook implements ShutdownHookProvider {
    @Override
    public void onShutdown() {
        FaucetStartupHook.addresses.cleanUp();
    }
}
