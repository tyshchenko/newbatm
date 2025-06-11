package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.ankerpay;

import com.generalbytes.batm.server.extensions.IExchange;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

public class AnkerpayExchange implements IExchange {
    private final String apiKey;
    private final String apiSecret;
    private final String preferredFiatCurrency;

    public AnkerpayExchange(String apiKey, String apiSecret, String preferredFiatCurrency) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.preferredFiatCurrency = preferredFiatCurrency;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        // Only BTC supported for now
        return Collections.singleton("BTC");
    }

    @Override
    public Set<String> getFiatCurrencies() {
        // Only USD supported for now
        return Collections.singleton("USD");
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        // Stub: Implement API call to Ankerpay to get crypto balance
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getFiatBalance(String fiatCurrency) {
        // Stub: Implement API call to Ankerpay to get fiat balance
        return BigDecimal.ZERO;
    }

    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        // Stub: Implement API call to Ankerpay to purchase coins
        return "stub-purchase-id";
    }

    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        // Stub: Implement API call to Ankerpay to sell coins
        return "stub-sell-id";
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        // Stub: Implement API call to Ankerpay to send coins
        return "stub-send-id";
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        // Stub: Implement API call to Ankerpay to get deposit address
        return "stub-deposit-address";
    }
}
