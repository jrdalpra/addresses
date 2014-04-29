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
import com.zeroturnaround.rebellabs.addresses.api.LocalesRepository;
import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.Locale.Type;
import com.zeroturnaround.rebellabs.addresses.model.State;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Resource
@ExtensionMethod({ Numbers.class })
public class LocalesController {

    @XStreamAlias("locale")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocaleResource implements HypermediaResource {

        private Locale entity;

        @Override
        public void configureRelations(RelationBuilder builder) {
            builder.relation("self").uses(LocalesController.class).get(entity);
            builder.relation("state").uses(StatesController.class).get(entity.getState());
            builder.relation("neighborhoods").uses(NeighborhoodsController.class).listRelatedWith(entity, 0, 10);
            builder.relation("publicplaces").uses(PublicPlacesController.class).listRelatedWith(entity, 0, 10);
        }

        public String getName() {
            return entity.getName();
        }

        public Type getType() {
            return entity.getType();
        }

    }

    @XStreamAlias("entities")
    @NoArgsConstructor
    public static class LocalesResources implements HypermediaResource {

        @XStreamOmitField
        private List<Locale>         entities;

        @XStreamOmitField
        private Integer              page;

        @XStreamOmitField
        private Integer              max;

        private List<LocaleResource> resources;

        private BuildRelations       relations;

        public LocalesResources(List<Locale> entities,
                                BuildRelations relations) {
            this.entities = entities;
            this.relations = relations;
            convert(entities);
        }

        private void convert(List<Locale> entities) {
            this.resources = new ArrayList<>(entities == null ? 0 : entities.size());
            for (Locale entity : entities)
                this.resources.add(new LocaleResource(entity));
        }

        @Override
        public void configureRelations(RelationBuilder builder) {
            if (relations != null)
                relations.buildUsingThe(builder);
        }

        @XStreamSerialize
        public List<LocaleResource> getResources() {
            return resources;
        }

    }

    @Inject
    private Result            result;

    @Inject
    private LocalesRepository repository;

    @Get
    @Path("/locales/{entity.id}")
    public void get(Locale entity) {
        result.use(representation()).from(new LocaleResource(repository.reload(entity))).serialize();
    }

    @Get
    @Path({ "/locales?page={page}&max={max}", "/locales" })
    public void list(Integer page, Integer max) {
        page = page.orWhenNull(0);
        max = max.orWhenNull(10);
        result.use(representation()).from(new LocalesResources(repository.list(page, max), pageLinks(page, max))).serialize();
    }

    private BuildRelations pageLinks(final Integer page, final Integer max) {
        return new BuildRelations() {

            @Override
            public void buildUsingThe(RelationBuilder builder) {
                builder.relation("self").uses(LocalesController.class).list(page, max);
                builder.relation("next").uses(LocalesController.class).list(page + 1, max);
                builder.relation("last").uses(LocalesController.class).list(repository.lastPage(max), max);
            }
        };
    }

    @Get
    @Path({ "/states/{state.id}/locales?type={type}&page={page}&max={max}", "/states/{state.id}/locales" })
    public void listByStateAndType(State state, Type type, Integer page, Integer max) {
        page = page.orWhenNull(0);
        max = max.orWhenNull(10);
        result.use(representation())
              .from(new LocalesResources(repository.listByStateAndType(state, type, page, max), pageLinks(state, type, page, max)))
              .serialize();
    }

    private BuildRelations pageLinks(final State state, final Type type, final Integer page, final Integer max) {
        return new BuildRelations() {

            @Override
            public void buildUsingThe(RelationBuilder builder) {
                builder.relation("self").uses(LocalesController.class).listByStateAndType(state, type, page, max);
                builder.relation("next").uses(LocalesController.class).listByStateAndType(state, type, page + 1, max);
                builder.relation("last").uses(LocalesController.class).listByStateAndType(state, type, repository.lastPage(state, type, max), max);
            }
        };
    }
}
