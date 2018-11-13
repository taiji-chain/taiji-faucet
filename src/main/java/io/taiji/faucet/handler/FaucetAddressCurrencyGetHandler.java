
package io.taiji.faucet.handler;

import com.networknt.config.Config;
import com.networknt.handler.LightHttpHandler;
import com.networknt.monad.Result;
import com.networknt.taiji.client.TaijiClient;
import com.networknt.taiji.crypto.SignedLedgerEntry;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Get all the transactions from chain-reader for an address and currency pair
 *
 * @author Steve Hu
 */
public class FaucetAddressCurrencyGetHandler implements LightHttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String address = exchange.getQueryParameters().get("address").getFirst();
        String currency = exchange.getQueryParameters().get("currency").getFirst();
        Result<List<SignedLedgerEntry>> result = TaijiClient.getTransaction(address, currency);
        if(result.isSuccess()) {
            List<SignedLedgerEntry> entries = result.getResult();
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(Config.getInstance().getMapper().writeValueAsString(entries));
        } else {
            setExchangeStatus(exchange, result.getError());
        }
    }
}
