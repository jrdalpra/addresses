package com.zeroturnaround.rebellabs.addresses.jaxrs.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.ExtensionMethod;

import com.zeroturnaround.rebellabs.addresses.api.CountriesRepository;
import com.zeroturnaround.rebellabs.addresses.jaxrs.jaxbutils.JaxRSLinkToBindableLink;
import com.zeroturnaround.rebellabs.addresses.model.Country;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Path("countries")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@ExtensionMethod({ Numbers.class })
public class CountriesController {

    @XmlType(name = "entities")
    @XmlRootElement(name = "entities")
    @XmlAccessorType(XmlAccessType.FIELD)
    @NoArgsConstructor
    @Getter
    public static class CountryResource {

        private Country    entity;

        @XmlJavaTypeAdapter(JaxRSLinkToBindableLink.class)
        private List<Link> links;

        public CountryResource(Country entity) {
            this.entity = entity;
        }

    }

    @XmlType(name = "entities")
    @XmlRootElement(name = "entities")
    @XmlAccessorType(XmlAccessType.FIELD)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class CountriesResources {
        @XmlElement(name = "country")
        @XmlElementWrapper(name = "content")
        private List<CountryResource> content;

        @XmlJavaTypeAdapter(JaxRSLinkToBindableLink.class)
        private List<Link>            links;

        public CountriesResources(List<Country> content,
                                  Link... links) {
            this.links = Arrays.asList(links);
            this.content = new ArrayList<>(content.size());
            register(content);
        }

        private void register(List<Country> content) {
            for (Country country : content)
                this.content.add(new CountryResource(country));
        }
    }

    @Inject
    private CountriesRepository countries;

    @Context
    private UriInfo             uriinfo;

    @GET
    @Path("/")
    public Response list(@QueryParam("page") Integer page,
                         @QueryParam("max") Integer max) {
        page = page.orWhenNull(0);
        max = max.orWhenNull(10);
        return Response.ok(new CountriesResources(countries.list(page, max), pageLinks(page, max))).build();
    }

    private Link[] pageLinks(Integer page, Integer max) {
        return new Link[] {
                listLinkTo("self", page, max),
                listLinkTo("next", page + 1, max),
                listLinkTo("last", countries.lastPage(max), max) };
    }

    private Link listLinkTo(String rel, Integer page, Integer max) {
        return Link.fromUriBuilder(toListMethod(page, max)).rel(rel).build();
    }

    private UriBuilder toListMethod(Integer page, Integer max) {
        return uriinfo.getAbsolutePathBuilder()
                      .path(CountriesController.class, "list")
                      .queryParam("page", page)
                      .queryParam("max", max);
    }
}
