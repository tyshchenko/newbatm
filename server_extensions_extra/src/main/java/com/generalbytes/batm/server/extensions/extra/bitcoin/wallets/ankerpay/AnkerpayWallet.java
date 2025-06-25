package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.ankerpay;


import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import si.mazi.rescu.RestProxyFactory;



public class AnkerpayWallet implements IWallet, IGeneratesNewDepositCryptoAddress{
    private static final Logger log = LoggerFactory.getLogger(AnkerpayWallet.class);
    private LocalAPI api;

    public AnkerpayWallet() {
        api = RestProxyFactory.createProxy(LocalAPI.class, "http://127.0.0.1:8099/");
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("wallet error: unknown cryptocurrency.");
            return null;
        }
        String rcryptoCurrency = cryptoCurrency;
        if (CryptoCurrency.USDTTRON.getCode().equalsIgnoreCase(cryptoCurrency)) {
            rcryptoCurrency = "USDTTRC20";
        }

        StatusRequest amount = new StatusRequest();
        amount.setAmount(new BigDecimal(1));
        final AddressData address = api.getAddress(rcryptoCurrency, amount);
        return address.getAddress();
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.ETH.getCode());
        result.add(CryptoCurrency.USDC.getCode());
        result.add(CryptoCurrency.USDT.getCode());
        result.add(CryptoCurrency.USDTTRON.getCode());
        result.add(CryptoCurrency.TRX.getCode());
        result.add(CryptoCurrency.BNB.getCode());
        return result;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CryptoCurrency.BTC.getCode(); // Optionally, you can make this configurable
    }

    public BalanceData getStatus(String address, String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("wallet error: unknown cryptocurrency.");
            return null;
        }
        String rcryptoCurrency = cryptoCurrency;

        if (CryptoCurrency.USDTTRON.getCode().equalsIgnoreCase(cryptoCurrency)) {
            rcryptoCurrency = "USDTTRC20";
        }
        final BalanceData balance = api.getStatus(rcryptoCurrency, address);
        return balance;
    }

    public String getNewCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("wallet error: unknown cryptocurrency.");
            return null;
        }
        StatusRequest amount = new StatusRequest();
        amount.setAmount(new BigDecimal(1));
        String rcryptoCurrency = cryptoCurrency;
        if (CryptoCurrency.USDTTRON.getCode().equalsIgnoreCase(cryptoCurrency)) {
            rcryptoCurrency = "USDTTRC20";
        }

        final AddressData address = api.getAddress(rcryptoCurrency, amount);
        return address.getAddress();
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("wallet error: unknown cryptocurrency.");
            return null;
        }
        String rcryptoCurrency = cryptoCurrency;

        if (CryptoCurrency.USDTTRON.getCode().equalsIgnoreCase(cryptoCurrency)) {
            rcryptoCurrency = "USDTTRC20";
        }
        final BalanceData balance = api.getBalanse(rcryptoCurrency);
        return balance.getBalance();
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("wallet error: unknown cryptocurrency.");
            return null;
        }
        String rcryptoCurrency = cryptoCurrency;
        if (CryptoCurrency.USDTTRON.getCode().equalsIgnoreCase(cryptoCurrency)) {
            rcryptoCurrency = "USDTTRC20";
        }

        SendToRequest senddata = new SendToRequest(amount, destinationAddress);

        final BalanceData balance = api.sendTo(rcryptoCurrency, senddata);
        return balance.getStatus();
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        log.info("generateNewDepositCryptoAddress {}",label);
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("wallet error: unknown cryptocurrency.");
            return null;
        }
        String rcryptoCurrency = cryptoCurrency;

        if (CryptoCurrency.USDTTRON.getCode().equalsIgnoreCase(cryptoCurrency)) {
            rcryptoCurrency = "USDTTRC20";
        }

        final AddressData address = api.getAddressWithLabel(rcryptoCurrency, new LNAddressRequest(new BigDecimal(1), label));
        return address.getAddress();
    }
}
