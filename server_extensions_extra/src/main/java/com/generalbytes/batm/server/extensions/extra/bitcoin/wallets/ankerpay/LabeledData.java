package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.ankerpay;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LabeledData {

    @JsonProperty("balance")
    private BigDecimal balance;

    @JsonProperty("confirmation")
    private String confirmation;



    public BigDecimal getBalance() {
        return balance;
    }

    public int getConfirmation() {
        return Integer.parseInt(confirmation);
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
