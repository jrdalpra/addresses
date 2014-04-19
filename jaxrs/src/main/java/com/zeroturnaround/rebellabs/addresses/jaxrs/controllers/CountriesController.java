package com.zeroturnaround.rebellabs.addresses.jaxrs.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import javax.xml.bind.annotation.XmlAttribute;
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
import com.zeroturnaround.rebellabs.addresses.model.Country;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Path("")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@ExtensionMethod({ Numbers.class })
public class CountriesController {

    @XmlType(name = "country")
    @XmlRootElement(name = "country")
    @XmlAccessorType(XmlAccessType.PROPERTY)
    @NoArgsConstructor
    public static class CountryResource {

        private Country    entity;

        private List<Link> links;

        private UriInfo    info;

        public CountryResource(Country entity,
                               UriInfo info) {
            this.entity = entity;
            this.info = info;
            this.links = new ArrayList<>();
            this.links.add(selfLink());
            this.links.add(statesLink());
        }

        private Link selfLink() {
            return Link.fromUriBuilder(this.info.getBaseUriBuilder()
                                                .path(CountriesController.class, "get"))
                       .rel("self")
                       .build(this.entity.getId());
        }

        private Link statesLink() {
            return Link.fromUriBuilder(this.info.getBaseUriBuilder()
                                                .path(StatesController.class, "listByCountry"))
                       .rel("states")
                       .build(this.entity.getId());
        }

        @XmlElement(name = "link")
        @XmlElementWrapper(name = "links")
        @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
        public List<Link> getLinks() {
            return links;
        }

        @XmlAttribute
        public String getAcronym() {
            return entity.getAcronym();
        }

        @XmlAttribute
        public String getName() {
            return entity.getName();
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

        @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
        private List<Link>            links;

        public CountriesResources(List<Country> content,
                                  UriInfo info,
                                  Link... links) {
            this.links = Arrays.asList(links);
            this.content = new ArrayList<>(content.size());
            register(content, info);
        }

        private void register(List<Country> content, UriInfo info) {
            for (Country country : content)
                this.content.add(new CountryResource(country, info));
        }
    }

    @Inject
    private CountriesRepository countries;

    @Context
    private UriInfo             info;

    @GET
    @Path("countries/{id}")
    public Response get(@PathParam("id") Country country) {
        return Response.ok(new CountryResource(countries.reload(country), info)).build();
    }

    @GET
    @Path("countries")
    public Response list(@QueryParam("page") @DefaultValue("0") Integer page,
                         @QueryParam("max") @DefaultValue("10") Integer max) {
        return Response.ok(new CountriesResources(countries.list(page, max), info, pageLinks(page, max))).build();
    }

    private Link[] pageLinks(Integer page, Integer max) {
        return new Link[] {
                listLinkTo("first", 0, max),
                listLinkTo("next", page + 1, max),
                listLinkTo("last", countries.lastPage(max), max)
        };
    }

    private Link listLinkTo(String rel, Integer page, Integer max) {
        return Link.fromUriBuilder(toListMethod(page, max)).rel(rel).build();
    }

    private UriBuilder toListMethod(Integer page, Integer max) {
        return info.getAbsolutePathBuilder()
                   .path(CountriesController.class, "list")
                   .queryParam("page", page)
                   .queryParam("max", max);
    }

}
