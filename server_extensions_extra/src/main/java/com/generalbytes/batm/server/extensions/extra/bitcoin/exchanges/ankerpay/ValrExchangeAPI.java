package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.ankerpay;

import java.math.BigDecimal;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import si.mazi.rescu.HttpStatusIOException;

@Path("v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ValrExchangeAPI {
    @GET
    @Path("/account/balances")
    List<ValrBalances> getBalance(@HeaderParam("X-VALR-API-KEY") String apiKey, @HeaderParam("X-VALR-SIGNATURE") String signature, @HeaderParam("X-VALR-TIMESTAMP") String timestamp) throws HttpStatusIOException;

    @GET
    @Path("/account/balances")
    String getBalanceTest(@HeaderParam("X-VALR-API-KEY") String apiKey, @HeaderParam("X-VALR-SIGNATURE") String signature, @HeaderParam("X-VALR-TIMESTAMP") String timestamp);

    @GET
    @Path("/wallet/crypto/{symbol}/deposit/address")
    ValrAddressData getAddress(@PathParam("symbol") String symbol, @HeaderParam("X-VALR-API-KEY") String apiKey, @HeaderParam("X-VALR-SIGNATURE") String signature, @HeaderParam("X-VALR-TIMESTAMP") String timestamp) throws HttpStatusIOException;

    @GET
    @Path("/wallet/crypto/{symbol}/deposit/address?networkType=TRON")
    ValrAddressData getTronAddress(@PathParam("symbol") String symbol, @HeaderParam("X-VALR-API-KEY") String apiKey, @HeaderParam("X-VALR-SIGNATURE") String signature, @HeaderParam("X-VALR-TIMESTAMP") String timestamp) throws HttpStatusIOException;

    @POST
    @Path("/wallet/crypto/{symbol}/withdraw")
    ValrRequestData sendMoney(ValrSend senddata, @PathParam("symbol") String cryptoCurrency, @HeaderParam("X-VALR-API-KEY") String apiKey, @HeaderParam("X-VALR-SIGNATURE") String signature, @HeaderParam("X-VALR-TIMESTAMP") String timestamp) throws HttpStatusIOException;

    @POST
    @Path("/wallet/crypto/{symbol}/withdraw")
    ValrRequestData sendMoneyTron(ValrSendTron senddata, @PathParam("symbol") String cryptoCurrency, @HeaderParam("X-VALR-API-KEY") String apiKey, @HeaderParam("X-VALR-SIGNATURE") String signature, @HeaderParam("X-VALR-TIMESTAMP") String timestamp) throws HttpStatusIOException;

    @POST
    @Path("/wallet/crypto/{symbol}/withdraw")
    ValrRequestData sendMoneyXRP(ValrSendXRP senddata, @PathParam("symbol") String cryptoCurrency, @HeaderParam("X-VALR-API-KEY") String apiKey, @HeaderParam("X-VALR-SIGNATURE") String signature, @HeaderParam("X-VALR-TIMESTAMP") String timestamp) throws HttpStatusIOException;

    @POST
    @Path("/orders/market")
    ValrOrderData createBuyOrder(ValrBuyOrder buyOrder, @HeaderParam("X-VALR-API-KEY") String apiKey, @HeaderParam("X-VALR-SIGNATURE") String signature, @HeaderParam("X-VALR-TIMESTAMP") String timestamp) throws HttpStatusIOException;

    @POST
    @Path("/orders/market")
    ValrOrderData createSellOrder(ValrSellOrder sellOrder, @HeaderParam("X-VALR-API-KEY") String apiKey, @HeaderParam("X-VALR-SIGNATURE") String signature, @HeaderParam("X-VALR-TIMESTAMP") String timestamp);

    @POST
    @Path("/orders/limit")
    ValrOrderData createLimitBuyOrder(ValrLimitBuyOrder buyOrder, @HeaderParam("X-VALR-API-KEY") String apiKey, @HeaderParam("X-VALR-SIGNATURE") String signature, @HeaderParam("X-VALR-TIMESTAMP") String timestamp);

    @POST
    @Path("/orders/limit")
    ValrOrderData createLimitSellOrder(ValrLimitSellOrder sellOrder, @HeaderParam("X-VALR-API-KEY") String apiKey, @HeaderParam("X-VALR-SIGNATURE") String signature, @HeaderParam("X-VALR-TIMESTAMP") String timestamp);

    @GET
    @Path("/public/{pair}/marketsummary")
    ValrTickerData getTicker(@PathParam("pair") String symbol);

}
