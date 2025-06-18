package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.ankerpay;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;

import si.mazi.rescu.RestProxyFactory;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.util.*;



public class AnkerpayRateSource implements IRateSourceAdvanced {
    private ValrAPI api;
    private String preferredFiatCurrency = FiatCurrency.ZAR.getCode();

    public AnkerpayRateSource(String preferredFiatCurrency) {
        api = RestProxyFactory.createProxy(ValrAPI.class, "https://api.valr.com");

        if (FiatCurrency.ZAR.getCode().equalsIgnoreCase(preferredFiatCurrency)) {
            this.preferredFiatCurrency = FiatCurrency.ZAR.getCode();
        }
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        // Support BTC, ETH, TRX, USDT, USDTTRON, USDC, BNB, SOL
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.ETH.getCode());
        result.add(CryptoCurrency.USDC.getCode());
        result.add(CryptoCurrency.USDT.getCode());
        result.add(CryptoCurrency.USDTTRON.getCode());
        result.add(CryptoCurrency.TRX.getCode());
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(FiatCurrency.ZAR.getCode());
        return result;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }


    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!getFiatCurrencies().contains(fiatCurrency)) {
            return null;
        }
        String pair = cryptoCurrency.toUpperCase() + "ZAR";

        if (CryptoCurrency.USDTTRON.getCode().equalsIgnoreCase(cryptoCurrency)) {
            pair = "USDTZAR";
        }
        final ValrTickerData cryptoZar = api.getTicker(pair);
        BigDecimal lastPriceInZar = cryptoZar.getAskPrice();
        return lastPriceInZar;
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        return getExchangeRateLast(cryptoCurrency, fiatCurrency);
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        if (!getFiatCurrencies().contains(fiatCurrency)) {
            return null;
        }
        String pair = cryptoCurrency.toUpperCase() + "ZAR";
        if (CryptoCurrency.USDTTRON.getCode().equalsIgnoreCase(cryptoCurrency)) {
            pair = "USDTZAR";
        }
        final ValrTickerData cryptoZar = api.getTicker(pair);
        BigDecimal lastPriceInZar = cryptoZar.getPrice();
        return lastPriceInZar;
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        final BigDecimal rate = getExchangeRateForBuy(cryptoCurrency, fiatCurrency);
        if (rate != null) {
            if (CryptoCurrency.USDC.getCode().equalsIgnoreCase(cryptoCurrency)) {
                return rate.multiply(cryptoAmount).setScale(2, RoundingMode.HALF_UP);
            } else {
                return rate.multiply(cryptoAmount);
            }
        }
        return null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        final BigDecimal rate = getExchangeRateForSell(cryptoCurrency, fiatCurrency);
        if (rate != null) {
            if (CryptoCurrency.USDC.getCode().equalsIgnoreCase(cryptoCurrency)) {
                return rate.multiply(cryptoAmount).setScale(2, RoundingMode.HALF_UP);
            } else if (CryptoCurrency.USDT.getCode().equalsIgnoreCase(cryptoCurrency)) {
                return rate.multiply(cryptoAmount).setScale(6, RoundingMode.HALF_UP);
            } else if (CryptoCurrency.USDTTRON.getCode().equalsIgnoreCase(cryptoCurrency)) {
                return rate.multiply(cryptoAmount).setScale(6, RoundingMode.HALF_UP);
            } else {
                return rate.multiply(cryptoAmount);
            }
        }
        return null;
    }
}
