package com.generalbytes.batm.server.extensions.extra.tron;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class TronDefinition extends CryptoCurrencyDefinition {
    private final IPaymentSupport paymentSupport = new TronPaymentSupport();

    public TronDefinition() {
        super(CryptoCurrency.TRX.getCode(), "TRX (TRON)", "tron", "https://tron.network");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
