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

import com.zeroturnaround.rebellabs.addresses.api.LocalesRepository;
import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.Locale.Type;
import com.zeroturnaround.rebellabs.addresses.model.State;

@Path("")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class LocalesController {

    @XmlType(name = "locale")
    @XmlRootElement(name = "locale")
    @XmlAccessorType(XmlAccessType.PROPERTY)
    @NoArgsConstructor
    @ToString
    public static class LocaleResource {

        private Locale     entity;

        private List<Link> links;

        private UriInfo    info;

        public LocaleResource(Locale entity,
                              UriInfo info) {
            this.entity = entity;
            this.info = info;
            this.links = new ArrayList<>();
            this.links.add(selfLink());
            this.links.add(stateLink());
        }

        private Link selfLink() {
            return Link.fromUriBuilder(info.getBaseUriBuilder().path(LocalesController.class, "get"))
                       .rel("self")
                       .build(entity.getId());
        }

        private Link stateLink() {
            return Link.fromUriBuilder(info.getBaseUriBuilder().path(StatesController.class, "get"))
                       .rel("state")
                       .build(entity.getState().getId());
        }

        @XmlElement(name = "link")
        @XmlElementWrapper(name = "links")
        @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
        public List<Link> getLinks() {
            return links;
        }

        @XmlAttribute
        public String getName() {
            return entity.getName();
        }

        @XmlAttribute
        public Type getType() {
            return entity.getType();
        }

    }

    @XmlType(name = "entities")
    @XmlRootElement(name = "entities")
    @XmlAccessorType(XmlAccessType.FIELD)
    @NoArgsConstructor
    @Getter
    public static class LocalesResources {

        @XmlElement(name = "locale")
        @XmlElementWrapper(name = "content")
        private List<LocaleResource> content;

        @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
        private List<Link>           links;

        @XmlTransient
        private UriInfo              info;

        public LocalesResources(List<Locale> entities,
                                UriInfo info,
                                Link... links) {
            this.info = info;
            this.links = new ArrayList<>();
            this.links.addAll(Arrays.asList(links));
            this.content = new ArrayList<>(entities.size());
            register(entities);
        }

        private void register(List<Locale> entities) {
            for (Locale entity : entities)
                content.add(new LocaleResource(entity, info));
        }

    }

    @Inject
    private LocalesRepository repository;

    @Context
    private UriInfo           info;

    @GET
    @Path("locales/{id}")
    public Response get(@PathParam("id") Locale entity) {
        return Response.ok(new LocaleResource(repository.reload(entity), info)).build();
    }

    @GET
    @Path("locales")
    public Response list(@QueryParam("page") @DefaultValue("0") Integer page,
                         @QueryParam("max") @DefaultValue("10") Integer max) {
        return Response.ok(new LocalesResources(repository.list(page, max), info, pageLinks(page, max))).build();
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
                   .path(LocalesController.class, "list")
                   .queryParam("page", page)
                   .queryParam("max", max);
    }

    @GET
    @Path("states/{id}/locales")
    public Response listByStateAndType(@PathParam("id") State state,
                                       @QueryParam("type") Type type,
                                       @QueryParam("page") @DefaultValue("0") Integer page,
                                       @QueryParam("max") @DefaultValue("10") Integer max) {
        return Response.ok(new LocalesResources(repository.listByStateAndType(state, type, page, max),
                                                info,
                                                pageLinks(state, type, page, max))).build();
    }

    private Link[] pageLinks(State state, Type type, Integer page, Integer max) {
        return new Link[] {
                linkToListByStateAndType("self", state, type, page, max),
                linkToListByStateAndType("next", state, type, page + 1, max),
                linkToListByStateAndType("last", state, type, repository.lastPage(state, type, max), max),
        };
    }

    private Link linkToListByStateAndType(String rel, State state, Type type, Integer page, Integer max) {
        return Link.fromUriBuilder(toListByStateAndType(type, page, max)).rel(rel).build(state.getId());
    }

    private UriBuilder toListByStateAndType(Type type, Integer page, Integer max) {
        return info.getBaseUriBuilder()
                   .path(LocalesController.class, "listByStateAndType")
                   .queryParam("type", type)
                   .queryParam("page", page)
                   .queryParam("max", max);
    }

}
