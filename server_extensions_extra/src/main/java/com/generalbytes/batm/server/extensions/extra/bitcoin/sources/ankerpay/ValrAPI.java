package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.ankerpay;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("v1/public")
@Produces(MediaType.APPLICATION_JSON)
public interface ValrAPI {
    @GET
    @Path("/{pair}/marketsummary")
    ValrTickerData getTicker(@PathParam("pair") String symbol);
}
