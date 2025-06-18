package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.ankerpay;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.math.BigDecimal;

public class ValrSellOrder {

    @JsonProperty("side")
    private String side;

    @JsonProperty("baseAmount")
    private String baseAmount;

    @JsonProperty("pair")
    private String pair;

    public void setPair(String pair) {
        this.pair = pair;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public void setAmount(String baseAmount) {
        this.baseAmount = baseAmount;
    }

}
