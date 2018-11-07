package io.taiji.faucet.handler;

import com.networknt.body.BodyHandler;
import com.networknt.config.Config;
import com.networknt.exception.ApiException;
import com.networknt.handler.LightHttpHandler;
import com.networknt.status.Status;
import com.networknt.taiji.client.TaijiClient;
import com.networknt.taiji.crypto.*;
import com.networknt.taiji.utility.Converter;
import io.taiji.faucet.FaucetConfig;
import io.taiji.faucet.FaucetStartupHook;
import io.taiji.faucet.model.Water;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.networknt.chain.utility.Console.exitError;

/**
 * populate up to 1000 Taiji coin to a specific address specified.
 *
 * @author Steve Hu
 */
public class FaucetAddressPostHandler implements LightHttpHandler {
    static final Logger logger = LoggerFactory.getLogger(FaucetAddressPostHandler.class);

    static final String INVALID_ADDRESS = "ERR12290";
    static final String RATE_LIMIT_REACHED = "ERR12291";
    static final String WALLET_CANNOT_OPEN = "ERR12292";
    static final String EMPTY_FAUCET_BODY = "ERR12293";
    static final String AMOUNT_EXCEED_MAX = "ERR12294";

    static final long maxShell = Converter.toShell(1000, Converter.Unit.TAIJI);

    static FaucetConfig config = (FaucetConfig) Config.getInstance().getJsonObjectConfig(FaucetConfig.CONFIG_NAME, FaucetConfig.class);

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String address = exchange.getQueryParameters().get("address").getFirst();
        // check if this address has asked for water within the 24 hours. If yes, it should
        // be in the cache addresses.
        Map<String, Long> currencyMap = FaucetStartupHook.addresses.getIfPresent(address);
        if(currencyMap != null) {
            setExchangeStatus(exchange, RATE_LIMIT_REACHED);
            return;
        }
        // parse the body of the post request.
        Map<String, Object> body = (Map<String, Object>)exchange.getAttachment(BodyHandler.REQUEST_BODY);
        Water water = Config.getInstance().getMapper().convertValue(body, Water.class);
        if(water == null) {
            setExchangeStatus(exchange, EMPTY_FAUCET_BODY);
            return;
        }
        long amountInShell = Converter.toShell((long)water.getAmount(), Converter.Unit.fromString(water.getUnit().toString()));
        if(amountInShell > maxShell) {
            setExchangeStatus(exchange, AMOUNT_EXCEED_MAX);
            return;
        }
        // verify that the address start with 0000, 0001 and 0002.
        String homeBank = address.substring(0, 4);
        switch(homeBank) {
            case "0000":
            case "0001":
            case "0002":
                // transfer fund
                Credentials credentials = getCredentials(config.getPassword(), Config.getInstance().getInputStreamFromFile(config.getAddress() + ".json"));
                if(credentials == null) {
                    setExchangeStatus(exchange, WALLET_CANNOT_OPEN);
                    return;
                }
                LedgerEntry ledgerEntry = new LedgerEntry(address, amountInShell);
                RawTransaction rtx = new RawTransaction(water.getCurrency().toString());
                rtx.addCreditEntry(address, ledgerEntry);
                rtx.addDebitEntry(config.getAddress(), ledgerEntry);
                SignedTransaction stx = TransactionManager.signTransaction(rtx, credentials);
                Status status = TaijiClient.postTx(config.getAddress().substring(0, 4), stx);
                if(status != null && status.getStatusCode() == 200) {
                    // get the snapshot for the target address and put into the cache.
                    try {
                        Map<String, Long> map = TaijiClient.getSnapshot(address);
                        FaucetStartupHook.addresses.put(address, map);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                        exchange.getResponseSender().send(Config.getInstance().getMapper().writeValueAsString(map));
                    } catch (ApiException e) {
                        setExchangeStatus(exchange, e.getStatus());
                        return;
                    }

                } else {
                    setExchangeStatus(exchange, status);
                    return;
                }
                break;
            default:
                setExchangeStatus(exchange, INVALID_ADDRESS, homeBank);
                return;
        }
    }

    private static Credentials getCredentials(String password, InputStream walletStream) {
        Credentials credentials = null;
        try {
            credentials = WalletUtils.loadCredentials(password, walletStream);
        } catch (CipherException e) {
            logger.error("Wrong password for wallet file:", e);
        } catch (IOException e) {
            logger.error("Unable to load wallet file from the stream:", e);
        }
        return credentials;
    }

}
