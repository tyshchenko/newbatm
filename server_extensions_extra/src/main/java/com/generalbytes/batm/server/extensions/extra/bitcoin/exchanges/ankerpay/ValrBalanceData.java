package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.ankerpay;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Iterator;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValrBalanceData {

    @JsonProperty
    private List<ValrBalances> balance;

    public List<ValrBalances> getBalances() {
        return balance;
    }

    public BigDecimal getBalance(String symbol) {
        final Logger log = LoggerFactory.getLogger("batm.master.exchange.valr");
        for (Iterator<ValrBalances> i = balance.iterator(); i.hasNext();) {
            ValrBalances item = i.next();
            if (item.getCurrency().equals(symbol)) {
                log.debug("{} balance = {}", item.getCurrency(), item.getBalance());
                return item.getBalance();
            }
        }
        return new BigDecimal("0.0");
    }

}
