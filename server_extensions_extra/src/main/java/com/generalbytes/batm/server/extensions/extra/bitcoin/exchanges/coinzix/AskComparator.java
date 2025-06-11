package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix.dto.entities.OrderBookPrice;

import java.math.BigDecimal;
import java.util.Comparator;

public class AskComparator implements Comparator<OrderBookPrice> {
    @Override
    public int compare(OrderBookPrice o1, OrderBookPrice o2) {
        BigDecimal dif = new BigDecimal(o1.rate).subtract(new BigDecimal(o2.rate));
        return dif.compareTo(BigDecimal.ZERO);
    }
}
