package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.ankerpay;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ValrTickerData {

    @JsonProperty("currencyPair")
    private String currencyPair;

    @JsonProperty("created")
    private String created;

    @JsonProperty("bidPrice")
    private BigDecimal bidPrice;

    @JsonProperty("askPrice")
    private BigDecimal askPrice;

    @JsonProperty("lastTradedPrice")
    private BigDecimal lastTradedPrice;

    @JsonProperty("baseVolume")
    private BigDecimal baseVolume;


    public BigDecimal getPrice() {
        return bidPrice;
    }

    public BigDecimal getAskPrice() {
        return askPrice;
    }

    public void setPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }
}
