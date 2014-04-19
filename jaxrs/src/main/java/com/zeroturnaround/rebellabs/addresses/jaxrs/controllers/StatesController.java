package com.zeroturnaround.rebellabs.addresses.jaxrs.controllers;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.zeroturnaround.rebellabs.addresses.api.StatesRepository;
import com.zeroturnaround.rebellabs.addresses.model.Country;

@Path("")
public class StatesController {

    @Inject
    private StatesRepository states;

    @Path("countries/{id}/states")
    public Response listByCountry(@PathParam("id") Country country,
                                  @QueryParam("page") @DefaultValue("0") Integer page,
                                  @QueryParam("max") @DefaultValue("10") Integer max) {
        return Response.ok(states.listWhereCountryEquals(country, page, max)).build();
    }

}
