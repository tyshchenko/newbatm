package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.ankerpay;


import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Iterator;

import si.mazi.rescu.RestProxyFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.ClientConfigUtil;
import si.mazi.rescu.HttpStatusIOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;


public class AnkerpayExchange implements IExchange {
    private String preferredFiatCurrency = FiatCurrency.ZAR.getCode();
    private String clientKey;
    private String clientSecret;
    private String typeorder;
    private ValrExchangeAPI api;
    private final Logger log;

    public AnkerpayExchange(String apiKey, String apiSecret, String typeorder) {
        this.preferredFiatCurrency = FiatCurrency.ZAR.getCode();
        this.clientKey = apiKey;
        this.clientSecret = apiSecret;
        this.typeorder = typeorder;
        log = LoggerFactory.getLogger("batm.master.exchange.valr");

        api = RestProxyFactory.createProxy(ValrExchangeAPI.class, "https://api.valr.com");
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

    public static String signRequest(String apiKeySecret, String timestamp, String verb, String path, String body) {
        try {
            Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(apiKeySecret.getBytes(), "HmacSHA512");
            hmacSHA512.init(secretKeySpec);
            hmacSHA512.update(timestamp.getBytes());
            hmacSHA512.update(verb.toUpperCase().getBytes());
            hmacSHA512.update(path.getBytes());
            hmacSHA512.update(body.getBytes());
            byte[] digest = hmacSHA512.doFinal();

            return toHexString(digest);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Unable to sign request", e);
        }
    }

    public static String toHexString(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public BigDecimal getBalance(String symbol, List<ValrBalances> balance) {
        for (Iterator<ValrBalances> i = balance.iterator(); i.hasNext();) {
            ValrBalances item = i.next();
            if (item.getCurrency().equals(symbol)) {
                log.debug("{} balance = {}", item.getCurrency(), item.getBalance());
                return item.getBalance();
            }
        }
        return new BigDecimal("0.0");
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {

        if (CryptoCurrency.USDTTRON.getCode().equalsIgnoreCase(cryptoCurrency)) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String signature = signRequest(clientSecret, timestamp, "GET", "/v1/wallet/crypto/USDT/deposit/address?networkType=TRON", "");
            try {
                final ValrAddressData address = api.getTronAddress("USDT", clientKey, signature, timestamp);
                return address.getAddress();
            } catch (HttpStatusIOException e) {
                log.error("Error {} crypto {}", e.getHttpBody(), cryptoCurrency );
                return null;
            }
        } else {

            String rightcryptoCurrency = cryptoCurrency;

            String timestamp = String.valueOf(System.currentTimeMillis());
            String signature = signRequest(clientSecret, timestamp, "GET", "/v1/wallet/crypto/"+rightcryptoCurrency+"/deposit/address", "");
            try {
                final ValrAddressData address = api.getAddress(rightcryptoCurrency, clientKey, signature, timestamp);
                return address.getAddress();
            } catch (HttpStatusIOException e) {
                log.error("Error {} crypto {}", e.getHttpBody(), cryptoCurrency );
                return null;
            }
        }
    }


    @Override
    public BigDecimal getFiatBalance(String fiatCurrency) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = signRequest(clientSecret, timestamp, "GET", "/v1/account/balances", "");
        try {
            final List<ValrBalances> balance = api.getBalance(clientKey, signature, timestamp);
            final BigDecimal fiatballance = getBalance("ZAR",balance);

            return fiatballance;
        } catch (HttpStatusIOException e) {
            log.error("Error {}", e.getHttpBody());
            return null;
        }
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = signRequest(clientSecret, timestamp, "GET", "/v1/account/balances", "");
        String rightcryptoCurrency = cryptoCurrency;

        if (CryptoCurrency.USDTTRON.getCode().equalsIgnoreCase(cryptoCurrency)) {
            rightcryptoCurrency = "USDT";
        }
        try {
            final List<ValrBalances> balance = api.getBalance(clientKey, signature, timestamp);
            BigDecimal cryptoballance;
            cryptoballance = getBalance(rightcryptoCurrency,balance);
            log.debug("{} exbalance = {}", rightcryptoCurrency, cryptoballance);
            return cryptoballance;
        } catch (HttpStatusIOException e) {
            log.error("Error {}", e.getHttpBody());
            return null;
        }
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        try {
            Thread.sleep(2000); //give exchange 2 seconds to reflect open order in order book
        } catch (InterruptedException e) {
            log.error("Error", e);
        }
        String rightcryptoCurrency = cryptoCurrency;
        if (CryptoCurrency.USDTTRON.getCode().equalsIgnoreCase(cryptoCurrency)) {
            rightcryptoCurrency = "USDT";
        }
        String timestamp = String.valueOf(System.currentTimeMillis());
        log.debug("sendMoney {} to {} amount {}  ", cryptoCurrency, destinationAddress, amount.toString());
        try {
            if (CryptoCurrency.USDC.getCode().equalsIgnoreCase(cryptoCurrency)) {
                amount = amount.setScale(2, BigDecimal.ROUND_CEILING);
                String signature = signRequest(clientSecret, timestamp, "POST", "/v1/wallet/crypto/"+rightcryptoCurrency+"/withdraw", "{\"address\":\""+destinationAddress+"\",\"amount\":\""+amount.toString()+"\"}");
                ValrSend senddata = new ValrSend();
                senddata.setAddress(destinationAddress);
                senddata.setAmount(amount.toString());
                final ValrRequestData result = api.sendMoney(senddata, rightcryptoCurrency, clientKey, signature, timestamp);
                return result.getResult();
            } else if (CryptoCurrency.USDT.getCode().equalsIgnoreCase(cryptoCurrency)) {
                amount = amount.setScale(2, BigDecimal.ROUND_CEILING);
                String signature = signRequest(clientSecret, timestamp, "POST", "/v1/wallet/crypto/"+rightcryptoCurrency+"/withdraw", "{\"address\":\""+destinationAddress+"\",\"amount\":\""+amount.toString()+"\"}");
                ValrSend senddata = new ValrSend();
                senddata.setAddress(destinationAddress);
                senddata.setAmount(amount.toString());
                final ValrRequestData result = api.sendMoney(senddata, rightcryptoCurrency, clientKey, signature, timestamp);
                return result.getResult();
            } else if (CryptoCurrency.USDTTRON.getCode().equalsIgnoreCase(cryptoCurrency)) {
                amount = amount.setScale(2, BigDecimal.ROUND_CEILING);
                String signature = signRequest(clientSecret, timestamp, "POST", "/v1/wallet/crypto/USDT/withdraw", "{\"address\":\""+destinationAddress+"\",\"amount\":\""+amount.toString()+"\",\"networkType\":\"TRON\"}");
                ValrSendTron senddata = new ValrSendTron();
                senddata.setAddress(destinationAddress);
                senddata.setAmount(amount.toString());
                senddata.setNetworkType("TRON");
                final ValrRequestData result = api.sendMoneyTron(senddata, rightcryptoCurrency, clientKey, signature, timestamp);
                return result.getResult();
            } else {
                String signature = signRequest(clientSecret, timestamp, "POST", "/v1/wallet/crypto/"+rightcryptoCurrency+"/withdraw", "{\"address\":\""+destinationAddress+"\",\"amount\":\""+amount.toString()+"\"}");
                ValrSend senddata = new ValrSend();
                senddata.setAddress(destinationAddress);
                senddata.setAmount(amount.toString());
                final ValrRequestData result = api.sendMoney(senddata, rightcryptoCurrency, clientKey, signature, timestamp);
                return result.getResult();
            }
        } catch (HttpStatusIOException e) {
            log.error("Error {} crypto {}", e.getHttpBody(), cryptoCurrency );
            return null;
        }
    }

    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        String type = "BUY";
        String pair;

        pair = cryptoCurrency.toUpperCase() + "ZAR";
        BigDecimal one       = new BigDecimal(1);
        BigDecimal onepr     = new BigDecimal(1.01);
        BigDecimal cryptofee    = new BigDecimal(0.00033);
        try {

            if (CryptoCurrency.TRX.getCode().equalsIgnoreCase(cryptoCurrency)) {
                final ValrTickerData trxTousdt = api.getTicker("TRXUSDT");
                BigDecimal trxusdtpricebid  = trxTousdt.getAsk();
                final ValrTickerData usdtToZar = api.getTicker("USDTZAR");
                BigDecimal usdtToZarpricebid  = usdtToZar.getAsk();
                BigDecimal trxcryptofee    = new BigDecimal(0.002);
                BigDecimal trxprocent      = new BigDecimal(1.005);
                //amount in dash
                BigDecimal trxamount  = amount.multiply(trxprocent);
                trxamount  = trxamount.add(trxcryptofee).setScale(6, BigDecimal.ROUND_CEILING);
                BigDecimal usdtamount   = trxusdtpricebid.multiply(trxamount).setScale(6, BigDecimal.ROUND_CEILING);
                BigDecimal zaramount   = usdtToZarpricebid.multiply(usdtamount).add(one).setScale(2, BigDecimal.ROUND_CEILING);

                String timestamp = String.valueOf(System.currentTimeMillis());
                String signature = signRequest(clientSecret, timestamp, "POST", "/v1/orders/market", "{\"side\":\""+type+"\",\"quoteAmount\":\""+zaramount.toString()+"\",\"pair\":\"USDTZAR\"}");

                ValrBuyOrder buyUSDTOrder = new ValrBuyOrder();
                buyUSDTOrder.setPair("USDTZAR");
                buyUSDTOrder.setSide(type);
                buyUSDTOrder.setAmount(zaramount.toString());
                final ValrOrderData resultusdt = api.createBuyOrder(buyUSDTOrder, clientKey, signature, timestamp);
                log.debug("market pair {} type {} amount   {}  result {}", "USDTZAR", type, zaramount.toString(), resultusdt.getResult());

                timestamp = String.valueOf(System.currentTimeMillis());
                signature = signRequest(clientSecret, timestamp, "POST", "/v1/orders/market", "{\"side\":\""+type+"\",\"quoteAmount\":\""+usdtamount.toString()+"\",\"pair\":\"TRXUSDT\"}");
                ValrBuyOrder buyTrxOrder = new ValrBuyOrder();
                buyTrxOrder.setPair("TRXUSDT");
                buyTrxOrder.setSide(type);
                buyTrxOrder.setAmount(usdtamount.toString());
                final ValrOrderData result = api.createBuyOrder(buyTrxOrder, clientKey, signature, timestamp);
                log.debug("market pair {} type {} amount   {}   result {}", "TRXUSDT", type, usdtamount.toString(), result.getResult());
                return result.getResult();
            } else if (CryptoCurrency.USDC.getCode().equalsIgnoreCase(cryptoCurrency)) {
                final ValrTickerData cryptoToZar = api.getTicker(pair);
                BigDecimal pricebid  = cryptoToZar.getAsk();
                amount               = amount.multiply(onepr);
                amount               = amount.add(one).setScale(0, BigDecimal.ROUND_CEILING);
                BigDecimal price     = pricebid;
                BigDecimal amountincrypto = price.multiply(amount).setScale(2, BigDecimal.ROUND_CEILING);
                String timestamp = String.valueOf(System.currentTimeMillis());

                String signature = signRequest(clientSecret, timestamp, "POST", "/v1/orders/market", "{\"side\":\""+type+"\",\"quoteAmount\":\""+amountincrypto.toString()+"\",\"pair\":\""+pair+"\"}");

                ValrBuyOrder buyOrder = new ValrBuyOrder();
                buyOrder.setPair(pair);
                buyOrder.setSide(type);
                buyOrder.setAmount(amountincrypto.toString());
                log.debug("market pair {} type {} amount   {}  ", pair, type, amountincrypto.toString());

                final ValrOrderData result = api.createBuyOrder(buyOrder, clientKey, signature, timestamp);
                log.debug("market pair {} type {} amount   {}  result {}", pair, type, amountincrypto.toString(), result.getResult());
                return result.getResult();
            } else if (CryptoCurrency.BNB.getCode().equalsIgnoreCase(cryptoCurrency)) {
                pair = "BNBZAR";
                final ValrTickerData cryptoToZar = api.getTicker(pair);
                BigDecimal pricebid  = cryptoToZar.getAsk();
                amount               = amount.multiply(onepr);
                amount               = amount.add(one).setScale(2, BigDecimal.ROUND_CEILING);
                BigDecimal price     = pricebid;
                BigDecimal amountincrypto = price.multiply(amount).setScale(2, BigDecimal.ROUND_CEILING);
                String timestamp = String.valueOf(System.currentTimeMillis());

                String signature = signRequest(clientSecret, timestamp, "POST", "/v1/orders/market", "{\"side\":\""+type+"\",\"quoteAmount\":\""+amountincrypto.toString()+"\",\"pair\":\""+pair+"\"}");

                ValrBuyOrder buyOrder = new ValrBuyOrder();
                buyOrder.setPair(pair);
                buyOrder.setSide(type);
                buyOrder.setAmount(amountincrypto.toString());
                log.debug("market pair {} type {} amount   {}  ", pair, type, amountincrypto.toString());

                final ValrOrderData result = api.createBuyOrder(buyOrder, clientKey, signature, timestamp);
                log.debug("market pair {} type {} amount   {}  result {}", pair, type, amountincrypto.toString(), result.getResult());
                return result.getResult();

            } else if (CryptoCurrency.USDTTRON.getCode().equalsIgnoreCase(cryptoCurrency)) {
                pair = "USDTZAR";
                final ValrTickerData cryptoToZar = api.getTicker(pair);
                BigDecimal pricebid  = cryptoToZar.getAsk();
                amount               = amount.multiply(onepr);
                amount               = amount.add(one).setScale(2, BigDecimal.ROUND_CEILING);
                BigDecimal price     = pricebid;
                BigDecimal amountincrypto = price.multiply(amount).setScale(2, BigDecimal.ROUND_CEILING);
                String timestamp = String.valueOf(System.currentTimeMillis());

                String signature = signRequest(clientSecret, timestamp, "POST", "/v1/orders/market", "{\"side\":\""+type+"\",\"quoteAmount\":\""+amountincrypto.toString()+"\",\"pair\":\""+pair+"\"}");

                ValrBuyOrder buyOrder = new ValrBuyOrder();
                buyOrder.setPair(pair);
                buyOrder.setSide(type);
                buyOrder.setAmount(amountincrypto.toString());
                log.debug("market pair {} type {} amount   {}  ", pair, type, amountincrypto.toString());

                final ValrOrderData result = api.createBuyOrder(buyOrder, clientKey, signature, timestamp);
                log.debug("market pair {} type {} amount   {}  result {}", pair, type, amountincrypto.toString(), result.getResult());
                return result.getResult();

            } else {
                final ValrTickerData cryptoToZar = api.getTicker(pair);
                BigDecimal pricebid  = cryptoToZar.getAsk();
                amount               = amount.multiply(onepr);
                amount               = amount.add(cryptofee).setScale(6, BigDecimal.ROUND_CEILING);
                BigDecimal price     = pricebid.add(one).setScale(0, BigDecimal.ROUND_CEILING);
                BigDecimal amountincrypto = price.multiply(amount).setScale(2, BigDecimal.ROUND_CEILING);
                String timestamp = String.valueOf(System.currentTimeMillis());

                String signature = signRequest(clientSecret, timestamp, "POST", "/v1/orders/market", "{\"side\":\""+type+"\",\"quoteAmount\":\""+amountincrypto.toString()+"\",\"pair\":\""+pair+"\"}");

                ValrBuyOrder buyOrder = new ValrBuyOrder();
                buyOrder.setPair(pair);
                buyOrder.setSide(type);
                buyOrder.setAmount(amountincrypto.toString());
                log.debug("market pair {} type {} amount   {}  ", pair, type, amountincrypto.toString());

                final ValrOrderData result = api.createBuyOrder(buyOrder, clientKey, signature, timestamp);
                log.debug("market pair {} type {} amount   {}  result {}", pair, type, amountincrypto.toString(), result.getResult());
                return result.getResult();
            }
        } catch (HttpStatusIOException e) {
            log.error("Error {} crypto {}", e.getHttpBody(), cryptoCurrency );
            return null;
        }
    }


    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        String type = "SELL";
        String pair;
        pair = cryptoCurrency.toUpperCase() + "ZAR";
        String timestamp = String.valueOf(System.currentTimeMillis());

        if (CryptoCurrency.TRX.getCode().equalsIgnoreCase(cryptoCurrency)) {
            // cryptoAmount in dash
            final ValrTickerData trxTousdt = api.getTicker("TRXUSDT");
            BigDecimal trxTousdtprice  = trxTousdt.getBid();
            BigDecimal usdtamount = trxTousdtprice.multiply(cryptoAmount).setScale(6, BigDecimal.ROUND_CEILING);

            String signature = signRequest(clientSecret, timestamp, "POST", "/v1/orders/market", "{\"side\":\""+type+"\",\"baseAmount\":\""+cryptoAmount.toString()+"\",\"pair\":\"TRXUSDT\"}");
            ValrSellOrder sellOrder = new ValrSellOrder();
            sellOrder.setPair("TRXUSDT");
            sellOrder.setSide(type);
            sellOrder.setAmount(cryptoAmount.toString());

            final ValrOrderData resultusdt = api.createSellOrder(sellOrder, clientKey, signature, timestamp);
            log.debug("market pair {} type {} amount   {}   result {}", "TRXUSDT", type, cryptoAmount.toString(), resultusdt.getResult());

            timestamp = String.valueOf(System.currentTimeMillis());
            signature = signRequest(clientSecret, timestamp, "POST", "/v1/orders/market", "{\"side\":\""+type+"\",\"baseAmount\":\""+usdtamount.toString()+"\",\"pair\":\"BTCZAR\"}");
            ValrSellOrder sellbtcOrder = new ValrSellOrder();
            sellbtcOrder.setPair("USDTZAR");
            sellbtcOrder.setSide(type);
            sellbtcOrder.setAmount(usdtamount.toString());

            final ValrOrderData result = api.createSellOrder(sellbtcOrder, clientKey, signature, timestamp);
            log.debug("market pair {} type {} amount   {}   result {}", "USDTZAR", type, usdtamount.toString(), result.getResult());

            return result.getResult();

        } else {
            if (CryptoCurrency.USDTTRON.getCode().equalsIgnoreCase(cryptoCurrency)) {
                pair = "USDTZAR";
            }
            String signature = signRequest(clientSecret, timestamp, "POST", "/v1/orders/market", "{\"side\":\""+type+"\",\"baseAmount\":\""+cryptoAmount.toString()+"\",\"pair\":\""+pair+"\"}");
            ValrSellOrder sellOrder = new ValrSellOrder();
            sellOrder.setPair(pair);
            sellOrder.setSide(type);
            sellOrder.setAmount(cryptoAmount.toString());

            final ValrOrderData result = api.createSellOrder(sellOrder, clientKey, signature, timestamp);
            log.debug("market pair {} type {} amount   {}   result {}", pair, type, cryptoAmount.toString(), result.getResult());
            return result.getResult();
        }
    }
}
