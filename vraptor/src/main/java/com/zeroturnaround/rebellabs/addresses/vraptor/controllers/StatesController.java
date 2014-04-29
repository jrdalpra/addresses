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
import com.zeroturnaround.rebellabs.addresses.api.StatesRepository;
import com.zeroturnaround.rebellabs.addresses.model.Country;
import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.State;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Resource
@ExtensionMethod({ Numbers.class })
public class StatesController {

    @XStreamAlias("state")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StateResource implements HypermediaResource {

        private State entity;

        @Override
        public void configureRelations(RelationBuilder builder) {
            builder.relation("self").uses(StatesController.class).get(entity);
            builder.relation("country").uses(CountriesController.class).get(entity.getCountry());
            builder.relation("cities").uses(LocalesController.class).listByStateAndType(entity, Locale.Type.CITY, 0, 10);
            builder.relation("districts").uses(LocalesController.class).listByStateAndType(entity, Locale.Type.DISTRICT, 0, 10);
            builder.relation("villages").uses(LocalesController.class).listByStateAndType(entity, Locale.Type.VILLAGE, 0, 10);
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
    public static class StatesResources implements HypermediaResource {

        @XStreamOmitField
        private List<State>         entities;

        @XStreamOmitField
        private Integer             page;

        @XStreamOmitField
        private Integer             max;

        private List<StateResource> resources;

        private BuildRelations      relations;

        public StatesResources(List<State> entities,
                               BuildRelations relations) {
            this.entities = entities;
            this.relations = relations;
            convert(entities);
        }

        private void convert(List<State> entities) {
            this.resources = new ArrayList<>(entities == null ? 0 : entities.size());
            for (State entity : entities)
                this.resources.add(new StateResource(entity));
        }

        @Override
        public void configureRelations(RelationBuilder builder) {
            if (relations != null)
                relations.buildUsingThe(builder);
        }

        @XStreamSerialize
        public List<StateResource> getResources() {
            return resources;
        }

    }

    @Inject
    private Result           result;

    @Inject
    private StatesRepository repository;

    @Get
    @Path("/states/{entity.id}")
    public void get(State entity) {
        result.use(representation()).from(new StateResource(repository.reload(entity))).serialize();
    }

    @Get
    @Path({ "/states?page={page}&max={max}", "/states" })
    public void list(Integer page, Integer max) {
        page = page.orWhenNull(0);
        max = max.orWhenNull(10);
        result.use(representation()).from(new StatesResources(repository.list(page, max), pageLinks(page, max))).serialize();
    }

    private BuildRelations pageLinks(final Integer page, final Integer max) {
        return new BuildRelations() {

            @Override
            public void buildUsingThe(RelationBuilder builder) {
                builder.relation("self").uses(StatesController.class).list(page, max);
                builder.relation("next").uses(StatesController.class).list(page + 1, max);
                builder.relation("last").uses(StatesController.class).list(repository.lastPage(max), max);
            }
        };
    }

    @Get
    @Path({ "/countries/{country.id}/states?page={page}&max={max}", "/countries/{country.id}/states" })
    public void listByCountry(Country country,
                              Integer page,
                              Integer max) {
        page = page.orWhenNull(0);
        max = max.orWhenNull(10);
        result.use(representation())
              .from(new StatesResources(repository.listWhereCountryEquals(country, page, max), pageLinks(country, page, max)))
              .serialize();
    }

    private BuildRelations pageLinks(final Country country, final Integer page, final Integer max) {
        return new BuildRelations() {

            @Override
            public void buildUsingThe(RelationBuilder builder) {
                builder.relation("self").uses(StatesController.class).listByCountry(country, page, max);
                builder.relation("next").uses(StatesController.class).listByCountry(country, page + 1, max);
                builder.relation("last").uses(StatesController.class).listByCountry(country, repository.lastPage(country, max), max);
            }
        };
    }

}
