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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.zeroturnaround.rebellabs.addresses.api.StatesRepository;
import com.zeroturnaround.rebellabs.addresses.model.Country;
import com.zeroturnaround.rebellabs.addresses.model.State;
import com.zeroturnaround.rebellabs.addresses.model.Locale.Type;

@Path("")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class StatesController {

    @XmlType(name = "state")
    @XmlRootElement(name = "state")
    @XmlAccessorType(XmlAccessType.PROPERTY)
    @NoArgsConstructor
    @ToString
    public static class StateResource {

        private State      entity;

        private List<Link> links;

        private UriInfo    info;

        public StateResource(State entity,
                             UriInfo info) {
            this.entity = entity;
            this.info = info;
            this.links = new ArrayList<>();
            addLinks();
        }

        private void addLinks() {
            links.add(selfLink());
            links.add(countryLink());
            addLocalesLinks();
        }

        private Link selfLink() {
            return Link.fromUriBuilder(info.getBaseUriBuilder().path(StatesController.class, "get"))
                       .rel("self")
                       .build(entity.getId());
        }

        private Link countryLink() {
            return Link.fromUriBuilder(info.getBaseUriBuilder().path(CountriesController.class, "get"))
                       .rel("country")
                       .build(entity.getCountry().getId());
        }

        public void addLocalesLinks() {
            links.add(linkToLocalesOf(Type.CITY, "cities"));
            links.add(linkToLocalesOf(Type.DISTRICT, "districts"));
            links.add(linkToLocalesOf(Type.VILLAGE, "villages"));
        }

        private Link linkToLocalesOf(Type type, String rel) {
            return Link.fromUriBuilder(toListByStateAndType(type, 0, 10))
                       .rel(rel)
                       .build(entity.getId());
        }

        private UriBuilder toListByStateAndType(Type type, Integer page, Integer max) {
            return info.getBaseUriBuilder()
                       .path(LocalesController.class, "listByStateAndType")
                       .queryParam("type", type)
                       .queryParam("page", page)
                       .queryParam("max", max);
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
    @Getter
    public static class StatesResources {

        @XmlElement(name = "state")
        @XmlElementWrapper(name = "content")
        private List<StateResource> content;

        @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
        private List<Link>          links;

        @XmlTransient
        private UriInfo             info;

        public StatesResources(List<State> entities,
                               UriInfo info,
                               Link... links) {
            this.info = info;
            this.links = new ArrayList<>();
            this.links.addAll(Arrays.asList(links));
            this.content = new ArrayList<>(entities.size());
            register(entities);
        }

        private void register(List<State> entities) {
            for (State entity : entities)
                content.add(new StateResource(entity, info));
        }

    }

    @Inject
    private StatesRepository repository;

    @Context
    private UriInfo          info;

    @GET
    @Path("states/{id}")
    public Response get(@PathParam("id") State entity) {
        return Response.ok(new StateResource(repository.reload(entity), info)).build();
    }

    @GET
    @Path("states")
    public Response list(@QueryParam("page") @DefaultValue("0") Integer page,
                         @QueryParam("max") @DefaultValue("10") Integer max) {
        return Response.ok(new StatesResources(repository.list(page, max), info, pageLinks(page, max))).build();
    }

    public Link[] pageLinks(Integer page, Integer max) {
        return new Link[] {
                linkToList("first", 0, max),
                linkToList("next", page + 1, max),
                linkToList("last", repository.lastPage(max), max)
        };
    }

    private Link linkToList(String rel, Integer page, Integer max) {
        return Link.fromUriBuilder(toListMethod(page, max)).rel(rel).build();
    }

    private UriBuilder toListMethod(Integer page, Integer max) {
        return info.getBaseUriBuilder()
                   .path(StatesController.class, "list")
                   .queryParam("page", page)
                   .queryParam("max", max);
    }

    @GET
    @Path("countries/{id}/states")
    public Response listByCountry(@PathParam("id") Country country,
                                  @QueryParam("page") @DefaultValue("0") Integer page,
                                  @QueryParam("max") @DefaultValue("10") Integer max) {
        return Response.ok(new StatesResources(repository.listWhereCountryEquals(country, page, max),
                                               info,
                                               pageLinks(country, page, max))).build();
    }

    private Link[] pageLinks(Country country, Integer page, Integer max) {
        return new Link[] {
                linkToListByCountry("first", country, 0, max),
                linkToListByCountry("next", country, page + 1, max),
                linkToListByCountry("last", country, repository.lastPage(country, max), max)
        };
    }

    private Link linkToListByCountry(String rel, Country country, Integer page, Integer max) {
        return Link.fromUriBuilder(toListByStateMethod(page, max))
                   .rel(rel)
                   .build(country.getId());
    }

    private UriBuilder toListByStateMethod(Integer page, Integer max) {
        return info.getBaseUriBuilder()
                   .path(StatesController.class, "listByCountry")
                   .queryParam("page", page)
                   .queryParam("max", max);
    }
}
