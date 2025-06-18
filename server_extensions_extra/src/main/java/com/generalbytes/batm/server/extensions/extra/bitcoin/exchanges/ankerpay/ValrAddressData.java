package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.ankerpay;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.math.BigDecimal;

public class ValrAddressData {

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("address")
    private String address;

    public String getAddress() {
        return address;
    }
}
