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
import com.zeroturnaround.rebellabs.addresses.api.CountriesRepository;
import com.zeroturnaround.rebellabs.addresses.model.Country;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Resource
@ExtensionMethod({ Numbers.class })
public class CountriesController {

    @XStreamAlias("country")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountryResource implements HypermediaResource {

        private Country entity;

        @Override
        public void configureRelations(RelationBuilder builder) {
            builder.relation("self").uses(CountriesController.class).get(entity);
        }

        @XStreamSerialize
        public String getAcronym() {
            return entity.getAcronym();
        }

        @XStreamSerialize
        public String getName() {
            return entity.getName();
        }

    }

    @XStreamAlias("entities")
    @NoArgsConstructor
    public static class CountriesResources implements HypermediaResource {

        @XStreamOmitField
        private List<Country>         entities;

        @XStreamOmitField
        private Integer               page;

        @XStreamOmitField
        private Integer               max;

        private List<CountryResource> resources;

        private BuildRelations        relations;

        public CountriesResources(List<Country> entities,
                                  BuildRelations relations) {
            this.entities = entities;
            this.relations = relations;
            convert(entities);
        }

        private void convert(List<Country> entities) {
            this.resources = new ArrayList<>(entities == null ? 0 : entities.size());
            for (Country entity : entities)
                this.resources.add(new CountryResource(entity));
        }

        @Override
        public void configureRelations(RelationBuilder builder) {
            if (relations != null)
                relations.buildUsingThe(builder);
        }

        @XStreamSerialize
        public List<CountryResource> getResources() {
            return resources;
        }

    }

    @Inject
    private Result              result;

    @Inject
    private CountriesRepository repository;

    @Get
    @Path("/countries/{entity.id}")
    public void get(Country entity) {
        result.use(representation()).from(new CountryResource(repository.reload(entity))).serialize();
    }

    @Get
    @Path({ "/countries?page={page}&max={max}", "/countries" })
    public void list(Integer page, Integer max) {
        page = page.orWhenNull(0);
        max = max.orWhenNull(10);
        result.use(representation()).from(new CountriesResources(repository.list(page, max), pageLinks(page, max))).serialize();
    }

    private BuildRelations pageLinks(final Integer page, final Integer max) {
        return new BuildRelations() {

            @Override
            public void buildUsingThe(RelationBuilder builder) {
                builder.relation("self").uses(CountriesController.class).list(page, max);
                builder.relation("next").uses(CountriesController.class).list(page + 1, max);
                builder.relation("last").uses(CountriesController.class).list(repository.lastPage(max), max);
            }
        };
    }
}
