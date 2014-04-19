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

import com.zeroturnaround.rebellabs.addresses.api.NeighborhoodsRepository;
import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.Neighborhood;

@Path("")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class NeighborhoodsController {

    @XmlType(name = "neighborhood")
    @XmlRootElement(name = "neighborhood")
    @XmlAccessorType(XmlAccessType.PROPERTY)
    @NoArgsConstructor
    @ToString
    public static class NeighborhoodResource {

        private Neighborhood entity;

        private List<Link>   links;

        private UriInfo      info;

        public NeighborhoodResource(Neighborhood entity,
                                    UriInfo info) {
            this.entity = entity;
            this.info = info;
            this.links = new ArrayList<>();
            this.links.add(linkToSelf());
            this.links.add(linkToState());
        }

        private Link linkToSelf() {
            return Link.fromUriBuilder(info.getBaseUriBuilder().path(NeighborhoodsController.class, "get"))
                       .rel("self")
                       .build(entity.getId());
        }

        private Link linkToState() {
            return Link.fromUriBuilder(info.getBaseUriBuilder().path(LocalesController.class, "get"))
                       .rel("locale")
                       .build(entity.getLocale().getId());
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

    }

    @XmlType(name = "entities")
    @XmlRootElement(name = "entities")
    @XmlAccessorType(XmlAccessType.FIELD)
    @NoArgsConstructor
    @Getter
    public static class NeighborhoodsResources {

        @XmlElement(name = "neighborhood")
        @XmlElementWrapper(name = "content")
        private List<NeighborhoodResource> content;

        @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
        private List<Link>                 links;

        @XmlTransient
        private UriInfo                    info;

        public NeighborhoodsResources(List<Neighborhood> entities,
                                      UriInfo info,
                                      Link... links) {
            this.info = info;
            this.links = new ArrayList<>();
            this.links.addAll(Arrays.asList(links));
            this.content = new ArrayList<>(entities.size());
            register(entities);
        }

        private void register(List<Neighborhood> entities) {
            for (Neighborhood entity : entities)
                content.add(new NeighborhoodResource(entity, info));
        }

    }

    @Inject
    private NeighborhoodsRepository repository;

    @Context
    private UriInfo                 info;

    @GET
    @Path("neighborhoods/{id}")
    public Response get(@PathParam("id") Neighborhood entity) {
        return Response.ok(new NeighborhoodResource(repository.reload(entity), info)).build();
    }

    @GET
    @Path("neighborhoods")
    public Response list(@QueryParam("page") @DefaultValue("0") Integer page,
                         @QueryParam("max") @DefaultValue("10") Integer max) {
        return Response.ok(new NeighborhoodsResources(repository.list(page, max), info, pageLinks(page, max))).build();
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
                   .path(NeighborhoodsController.class, "list")
                   .queryParam("page", page)
                   .queryParam("max", max);
    }

    @GET
    @Path("locales/{id}/neighborhoods")
    public Response listRelatedWith(@PathParam("id") Locale locale,
                                    @QueryParam("page") @DefaultValue("0") Integer page,
                                    @QueryParam("max") @DefaultValue("10") Integer max) {
        return Response.ok().build();
    }

}
