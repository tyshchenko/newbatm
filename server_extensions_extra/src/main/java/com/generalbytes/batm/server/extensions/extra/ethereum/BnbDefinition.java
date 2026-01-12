package com.generalbytes.batm.server.extensions.extra.ethereum;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class BnbDefinition extends CryptoCurrencyDefinition{
    private final IPaymentSupport paymentSupport = new BnbPaymentSupport();

    public BnbDefinition() {
        super(CryptoCurrency.BNB.getCode(), "BNB BSC-20", "binance_smart_coin", "https://binance.com/");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
