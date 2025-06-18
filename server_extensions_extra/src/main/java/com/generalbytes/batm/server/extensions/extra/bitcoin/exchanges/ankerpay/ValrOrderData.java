package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.ankerpay;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.math.BigDecimal;

public class ValrOrderData {

    @JsonProperty("id")
    private String id;

    public String getResult() {
        return id;
    }
}
