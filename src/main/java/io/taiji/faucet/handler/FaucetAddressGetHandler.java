
package io.taiji.faucet.handler;

import com.networknt.config.Config;
import com.networknt.exception.ApiException;
import com.networknt.handler.LightHttpHandler;
import com.networknt.taiji.client.TaijiClient;
import io.taiji.faucet.FaucetStartupHook;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.Map;

/**
 * Get the balance for the address including all the currencies belong to the address.
 *
 * @author Steve Hu
 */
public class FaucetAddressGetHandler implements LightHttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // first check if the address is in the cache, if it is return it.
        String address = exchange.getQueryParameters().get("address").getFirst();
        // check if this address has asked for water within the 24 hours. If yes, it should
        // be in the cache addresses.
        Map<String, Long> currencyMap = FaucetStartupHook.addresses.getIfPresent(address);
        if(currencyMap != null) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(Config.getInstance().getMapper().writeValueAsString(currencyMap));
            return;
        }
        // otherwise, get it from the chain-reader service but don't put it into the cache as it is used for rate limiting.
        try {
            currencyMap = TaijiClient.getSnapshot(address);
            // put into the addresses cache, the cache will expire in 24 hours.
            FaucetStartupHook.addresses.put(address, currencyMap);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(Config.getInstance().getMapper().writeValueAsString(currencyMap));
        } catch (ApiException e) {
            setExchangeStatus(exchange, e.getStatus());
            return;
        }
    }
}
