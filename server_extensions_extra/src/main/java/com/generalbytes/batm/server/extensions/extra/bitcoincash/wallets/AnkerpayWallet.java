package com.generalbytes.batm.server.extensions.extra.bitcoincash.wallets;

import com.generalbytes.batm.server.extensions.IWallet;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

public class AnkerpayWallet implements IWallet {
    private final String address;
    private final String apiKey;
    private final String apiSecret;

    public AnkerpayWallet(String address, String apiKey, String apiSecret) {
        this.address = address;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        // Return the wallet's address for the given cryptoCurrency
        return address;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        // Only BCH supported for now
        return Collections.singleton("BCH");
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return "BCH";
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        // Stub: Implement API call to Ankerpay to get balance
        return BigDecimal.ZERO;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        // Stub: Implement API call to Ankerpay to send coins
        return "stub-txid";
    }
}
