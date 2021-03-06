
package io.taiji.faucet.handler;

import com.networknt.config.Config;
import com.networknt.handler.LightHttpHandler;
import com.networknt.monad.Result;
import com.networknt.taiji.client.TaijiClient;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.Map;

/**
 * Get the balance for the address including all the currencies belong to the address. Also all the
 * transactions for the account.
 *
 * @author Steve Hu
 */
public class FaucetAddressGetHandler implements LightHttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // first check if the address is in the cache, if it is return it.
        String address = exchange.getQueryParameters().get("address").getFirst();
        // otherwise, get it from the chain-reader service but don't put it into the cache as it is used for rate limiting.
        Result<Map<String, Long>> result = TaijiClient.getSnapshot(address);
        if(result.isSuccess()) {
            Map<String, Long> currencyMap = result.getResult();
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(Config.getInstance().getMapper().writeValueAsString(currencyMap));
        } else {
            setExchangeStatus(exchange, result.getError());
        }
    }
}
