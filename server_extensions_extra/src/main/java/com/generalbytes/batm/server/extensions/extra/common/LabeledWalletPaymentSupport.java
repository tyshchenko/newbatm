package com.generalbytes.batm.server.extensions.extra.common;

import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.ankerpay.AnkerpayWallet;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestSpecification;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;

import java.math.BigDecimal;
import java.util.List;

public abstract class LabeledWalletPaymentSupport extends PollingPaymentSupport {

    @Override
    public PaymentRequest createPaymentRequest(IPaymentRequestSpecification spec) {
//        AnkerpayWallet wallet = (AnkerpayWallet) spec.getWallet();
//        ReceivedAmount receivedAmount = wallet.getTokenBalance(spec.getOutputs().get(0).getAddress(), spec.getCryptoCurrency(), spec.getDescription(), spec.getTotal());

        return super.createPaymentRequest(spec);
    }

    @Override
    public void poll(PaymentRequest request) {
        try {
            AnkerpayWallet wallet = (AnkerpayWallet) request.getWallet();
            ReceivedAmount receivedAmount = wallet.getTokenBalance(request.getAddress(), request.getCryptoCurrency(), request.getDescription(), request.getAmount());

            BigDecimal totalReceived = receivedAmount.getTotalAmountReceived();
            int confirmations = receivedAmount.getConfirmations();

            if (totalReceived.compareTo(BigDecimal.ZERO) == 0) {
                return;
            }

            if (!receivedAmountMatchesRequestedAmountInTolerance(request, totalReceived)) {
                log.info("Received amount ({}) does not match the requested amount ({}), {}", totalReceived, request.getAmount(), request);
                // stop future polling
                setState(request, PaymentRequest.STATE_TRANSACTION_INVALID);
                return;
            }

            // correct amount received

            if (request.getState() == PaymentRequest.STATE_NEW) {
                log.info("Received: {}, amounts matches. {}", totalReceived, request);
                request.setTxValue(totalReceived);
                request.setIncomingTransactionHash(getTransactionHashesAsString(receivedAmount));
                setState(request, PaymentRequest.STATE_SEEN_TRANSACTION);
            }

            if (confirmations > 0) {
                if (request.getState() == PaymentRequest.STATE_SEEN_TRANSACTION) {
                    log.info("Transaction confirmed. {}", request);
                    setState(request, PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);
                }
                updateNumberOfConfirmations(request, confirmations);
            }


        } catch (Exception e) {
            log.error("", e);
        }
    }

    private String getTransactionHashesAsString(ReceivedAmount receivedAmount) {
        List<String> transactionHashes = receivedAmount.getTransactionHashes();
        if (transactionHashes != null && !transactionHashes.isEmpty()) {
            return String.join(" ", transactionHashes);
        }
        return null;
    }

    private boolean receivedAmountMatchesRequestedAmountInTolerance(PaymentRequest request, BigDecimal totalReceived) {
        BigDecimal requestedAmount = request.getAmount();
        BigDecimal tolerance = request.getTolerance();

        return totalReceived.compareTo(requestedAmount) == 0
            || (totalReceived.compareTo(requestedAmount) > 0 && request.isOverageAllowed())
            || (tolerance != null && totalReceived.subtract(requestedAmount).abs().compareTo(tolerance) <= 0);
    }
}
