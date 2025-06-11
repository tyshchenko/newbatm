package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.ankerpay;

import com.generalbytes.batm.server.extensions.IRateSource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

public class AnkerpayRateSource implements IRateSource {
    private final String apiKey;
    private final String apiSecret;
    private final String preferredFiatCurrency;

    public AnkerpayRateSource(String apiKey, String apiSecret, String preferredFiatCurrency) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.preferredFiatCurrency = preferredFiatCurrency;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        // Support BTC, ETH, TRX, USDT, USDTTRON, USDC, BNB, SOL
        return Set.of("BTC", "ETH", "TRX", "USDT", "USDTTRON", "USDC", "BNB", "SOL");
    }

    @Override
    public Set<String> getFiatCurrencies() {
        // Only USD supported for now
        return Collections.singleton("USD");
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        // Stub: Implement API call to Ankerpay to get the latest exchange rate
        return BigDecimal.ZERO;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }
}
