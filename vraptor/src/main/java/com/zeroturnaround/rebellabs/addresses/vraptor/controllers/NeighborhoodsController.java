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
import com.zeroturnaround.rebellabs.addresses.api.NeighborhoodsRepository;
import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.Neighborhood;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Resource
@ExtensionMethod({ Numbers.class })
public class NeighborhoodsController {

    @XStreamAlias("neighborhood")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NeighborhoodResource implements HypermediaResource {

        private Neighborhood entity;

        @Override
        public void configureRelations(RelationBuilder builder) {
            builder.relation("self").uses(NeighborhoodsController.class).get(entity);
            builder.relation("locale").uses(LocalesController.class).get(entity.getLocale());
        }

        @XStreamSerialize
        public String getName() {
            return entity.getName();
        }

    }

    @XStreamAlias("entities")
    @NoArgsConstructor
    public static class NeighborhoodsResources implements HypermediaResource {

        @XStreamOmitField
        private List<Neighborhood>         entities;

        @XStreamOmitField
        private Integer                    page;

        @XStreamOmitField
        private Integer                    max;

        private List<NeighborhoodResource> resources;

        private BuildRelations             relations;

        public NeighborhoodsResources(List<Neighborhood> entities,
                                      BuildRelations relations) {
            this.entities = entities;
            this.relations = relations;
            convert(entities);
        }

        private void convert(List<Neighborhood> entities) {
            this.resources = new ArrayList<>(entities == null ? 0 : entities.size());
            for (Neighborhood entity : entities)
                this.resources.add(new NeighborhoodResource(entity));
        }

        @Override
        public void configureRelations(RelationBuilder builder) {
            if (relations != null)
                relations.buildUsingThe(builder);
        }

        @XStreamSerialize
        public List<NeighborhoodResource> getResources() {
            return resources;
        }

    }

    @Inject
    private Result                  result;

    @Inject
    private NeighborhoodsRepository repository;

    @Get
    @Path("/neighborhoods/{entity.id}")
    public void get(Neighborhood entity) {
        result.use(representation()).from(new NeighborhoodResource(repository.reload(entity))).serialize();
    }

    @Get
    @Path({ "/neighborhoods?page={page}&max={max}", "/neighborhoods" })
    public void list(Integer page, Integer max) {
        page = page.orWhenNull(0);
        max = max.orWhenNull(10);
        result.use(representation()).from(new NeighborhoodsResources(repository.list(page, max), pageLinks(page, max))).serialize();
    }

    private BuildRelations pageLinks(final Integer page, final Integer max) {
        return new BuildRelations() {

            @Override
            public void buildUsingThe(RelationBuilder builder) {
                builder.relation("self").uses(NeighborhoodsController.class).list(page, max);
                builder.relation("next").uses(NeighborhoodsController.class).list(page + 1, max);
                builder.relation("last").uses(NeighborhoodsController.class).list(repository.lastPage(max), max);
            }
        };
    }

    @Path({ "/locales/{locale.id}/neighborhoods?page={page}&max={max}", "/locales/{locale.id}/neighborhoods" })
    public void listRelatedWith(Locale locale, Integer page, Integer max) {
        page = page.orWhenNull(0);
        max = max.orWhenNull(10);
        result.use(representation())
              .from(new NeighborhoodsResources(repository.listRelatedWith(locale, page, max), pageLinks(locale, page, max)))
              .serialize();
    }

    private BuildRelations pageLinks(final Locale locale, final Integer page, final Integer max) {
        return new BuildRelations() {

            @Override
            public void buildUsingThe(RelationBuilder builder) {
                builder.relation("self").uses(NeighborhoodsController.class).listRelatedWith(locale, page, max);
                builder.relation("next").uses(NeighborhoodsController.class).listRelatedWith(locale, page + 1, max);
                builder.relation("last").uses(NeighborhoodsController.class).listRelatedWith(locale, repository.lastPageRelatedWith(locale, max), max);
            }
        };
    }
}
