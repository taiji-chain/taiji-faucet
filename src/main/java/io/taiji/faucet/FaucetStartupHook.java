package io.taiji.faucet;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.networknt.config.Config;
import com.networknt.server.StartupHookProvider;
import com.networknt.taiji.crypto.CipherException;
import com.networknt.taiji.crypto.Credentials;
import com.networknt.taiji.crypto.WalletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * During the startup, create the cache for the snapshot and rate limiting.
 * Also, cache the credentials as it is very slow to load and decrypt.
 *
 * @author Steve Hu
 */
public class FaucetStartupHook implements StartupHookProvider {
    static Logger logger = LoggerFactory.getLogger(FaucetStartupHook.class);
    // cache the requests to enforce rate limit.
    public static Cache<String, Boolean> requests;

    public static Credentials credentials;

    public static FaucetConfig config = (FaucetConfig) Config.getInstance().getJsonObjectConfig(FaucetConfig.CONFIG_NAME, FaucetConfig.class);

    @Override
    public void onStartup() {

        credentials = getCredentials(config.getPassword(), Config.getInstance().getInputStreamFromFile(config.getAddress() + ".json"));

        // the entry will be remove after 24 hours since it is written.
        requests = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build();

    }

    private static com.networknt.taiji.crypto.Credentials getCredentials(String password, InputStream walletStream) {
        com.networknt.taiji.crypto.Credentials credentials = null;
        try {
            credentials = WalletUtils.loadCredentials(password, walletStream);
        } catch (CipherException e) {
            logger.error("Wrong password for wallet file:", e);
            throw new RuntimeException("Wrong password for wallet file:", e);
        } catch (IOException e) {
            logger.error("Unable to load wallet file from the stream:", e);
            throw new RuntimeException("Unable to load wallet file from the stream:", e);
        }
        return credentials;
    }
}
