package io.taiji.faucet.handler;

import com.networknt.body.BodyHandler;
import com.networknt.config.Config;
import com.networknt.handler.LightHttpHandler;
import com.networknt.status.Status;
import com.networknt.taiji.client.TaijiClient;
import com.networknt.taiji.crypto.*;
import com.networknt.taiji.utility.Converter;
import io.taiji.faucet.FaucetStartupHook;
import io.taiji.faucet.model.Water;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * populate up to 1000 Taiji coin to a specific address specified.
 *
 * @author Steve Hu
 */
public class FaucetAddressPostHandler implements LightHttpHandler {
    static final Logger logger = LoggerFactory.getLogger(FaucetAddressPostHandler.class);

    static final String INVALID_HOMEBANK = "ERR12290";
    static final String RATE_LIMIT_REACHED = "ERR12291";
    static final String EMPTY_FAUCET_BODY = "ERR12293";
    static final String AMOUNT_EXCEED_MAX = "ERR12294";
    static final String INVALID_ADDRESS = "ERR12295";

    static final String SUCCESS_OK = "SUC10200";

    static final long maxShell = Converter.toShell(1000, Converter.Unit.TAIJI);

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String address = exchange.getQueryParameters().get("address").getFirst();
        // validate the address
        if(!Keys.validateToAddress(address)) {
            setExchangeStatus(exchange, INVALID_ADDRESS, address);
            return;
        }
        // check if this address has asked for water within the 24 hours. If yes, it should be in requests cache
        Boolean b = FaucetStartupHook.requests.getIfPresent(address);
        if(b != null && b == true) {
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
                LedgerEntry ledgerEntry = new LedgerEntry(address, amountInShell);
                RawTransaction rtx = new RawTransaction(water.getCurrency());
                rtx.addCreditEntry(address, ledgerEntry);
                rtx.addDebitEntry(FaucetStartupHook.config.getAddress(), ledgerEntry);
                SignedTransaction stx = TransactionManager.signTransaction(rtx, FaucetStartupHook.credentials);
                Status status = TaijiClient.postTx(FaucetStartupHook.config.getAddress().substring(0, 4), stx);
                if(status != null && status.getStatusCode() == 200) {
                    // this is to prevent submit the request for the same address within the same day.
                    FaucetStartupHook.requests.put(address, true);
                    setExchangeStatus(exchange, SUCCESS_OK);
                    return;
                } else {
                    setExchangeStatus(exchange, status);
                    return;
                }
            default:
                setExchangeStatus(exchange, INVALID_HOMEBANK, homeBank);
                return;
        }
    }
}
