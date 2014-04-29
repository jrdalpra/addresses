package com.zeroturnaround.rebellabs.addresses.vraptor.controllers;

import static br.com.caelum.vraptor.view.Results.representation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.ExtensionMethod;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;
import br.com.caelum.vraptor.restfulie.relation.RelationBuilder;
import br.com.caelum.vraptor.restfulie.serialization.XStreamSerialize;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.zeroturnaround.rebellabs.addresses.api.PublicPlacesRepository;
import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.PublicPlace;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Resource
@ExtensionMethod({ Numbers.class })
public class PublicPlacesController {

    @XStreamAlias("publicplace")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PublicPlaceResource implements HypermediaResource {

        private PublicPlace entity;

        @Override
        public void configureRelations(RelationBuilder builder) {
            builder.relation("self").uses(PublicPlacesController.class).get(entity);
            builder.relation("locale").uses(LocalesController.class).get(entity.getLocale());
            builder.relation("type").uses(TypesOfPublicPlacesController.class).get(entity.getType());
            builder.relation("neighborhood").uses(NeighborhoodsController.class).get(entity.getNeighborhood());

        }

        @XStreamSerialize
        public String getName() {
            return entity.getName();
        }

    }

    @XStreamAlias("entities")
    @NoArgsConstructor
    public static class PublicPlacesResources implements HypermediaResource {

        @XStreamOmitField
        private List<PublicPlace>         entities;

        @XStreamOmitField
        private Integer                   page;

        @XStreamOmitField
        private Integer                   max;

        private List<PublicPlaceResource> resources;

        private BuildRelations            relations;

        public PublicPlacesResources(List<PublicPlace> entities,
                                     BuildRelations relations) {
            this.entities = entities;
            this.relations = relations;
            convert(entities);
        }

        private void convert(List<PublicPlace> entities) {
            this.resources = new ArrayList<>(entities == null ? 0 : entities.size());
            for (PublicPlace entity : entities)
                this.resources.add(new PublicPlaceResource(entity));
        }

        @Override
        public void configureRelations(RelationBuilder builder) {
            if (relations != null)
                relations.buildUsingThe(builder);
        }

        @XStreamSerialize
        public List<PublicPlaceResource> getResources() {
            return resources;
        }

    }

    @Inject
    private Result                 result;

    @Inject
    private PublicPlacesRepository repository;

    @Get
    @Path("/publicplaces/{entity.id}")
    public void get(PublicPlace entity) {
        result.use(representation()).from(new PublicPlaceResource(repository.reload(entity))).serialize();
    }

    @Get
    @Path({ "/publicplaces?page={page}&max={max}", "/publicplaces" })
    public void list(Integer page, Integer max) {
        page = page.orWhenNull(0);
        max = max.orWhenNull(10);
        result.use(representation()).from(new PublicPlacesResources(repository.list(page, max), pageLinks(page, max))).serialize();
    }

    private BuildRelations pageLinks(final Integer page, final Integer max) {
        return new BuildRelations() {

            @Override
            public void buildUsingThe(RelationBuilder builder) {
                builder.relation("self").uses(PublicPlacesController.class).list(page, max);
                builder.relation("next").uses(PublicPlacesController.class).list(page + 1, max);
                builder.relation("last").uses(PublicPlacesController.class).list(repository.lastPage(max), max);
            }
        };
    }

    @Path({ "/locales/{locale.id}/publicplaces?page={page}&max={max}", "/locales/{locale.id}/publicplaces" })
    public void listRelatedWith(Locale locale, Integer page, Integer max) {
        page = page.orWhenNull(0);
        max = max.orWhenNull(10);
        result.use(representation())
              .from(new PublicPlacesResources(repository.listRelatedWith(locale, page, max), pageLinks(locale, page, max)))
              .serialize();
    }

    private BuildRelations pageLinks(final Locale locale, final Integer page, final Integer max) {
        return new BuildRelations() {

            @Override
            public void buildUsingThe(RelationBuilder builder) {
                builder.relation("self").uses(PublicPlacesController.class).listRelatedWith(locale, page, max);
                builder.relation("next").uses(PublicPlacesController.class).listRelatedWith(locale, page + 1, max);
                builder.relation("last").uses(PublicPlacesController.class).listRelatedWith(locale, repository.lastPageRelatedWith(locale, max), max);
            }
        };
    }

}
