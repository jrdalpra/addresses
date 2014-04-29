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
import com.zeroturnaround.rebellabs.addresses.api.TypesOfPublicPlacesRepository;
import com.zeroturnaround.rebellabs.addresses.model.TypeOfPublicPlaces;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Resource
@ExtensionMethod({ Numbers.class })
public class TypesOfPublicPlacesController {

    @XStreamAlias("typeofpublicplace")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeOfPublicPlacesResource implements HypermediaResource {

        private TypeOfPublicPlaces entity;

        @Override
        public void configureRelations(RelationBuilder builder) {
            builder.relation("self").uses(TypesOfPublicPlacesController.class).get(entity);
        }

        @XStreamSerialize
        public String getName() {
            return entity.getName();
        }

    }

    @XStreamAlias("entities")
    @NoArgsConstructor
    public static class TypesOfPublicPlacesResources implements HypermediaResource {

        @XStreamOmitField
        private List<TypeOfPublicPlaces>         entities;

        @XStreamOmitField
        private Integer                          page;

        @XStreamOmitField
        private Integer                          max;

        private List<TypeOfPublicPlacesResource> resources;

        private BuildRelations                   relations;

        public TypesOfPublicPlacesResources(List<TypeOfPublicPlaces> entities,
                                            BuildRelations relations) {
            this.entities = entities;
            this.relations = relations;
            convert(entities);
        }

        private void convert(List<TypeOfPublicPlaces> entities) {
            this.resources = new ArrayList<>(entities == null ? 0 : entities.size());
            for (TypeOfPublicPlaces entity : entities)
                this.resources.add(new TypeOfPublicPlacesResource(entity));
        }

        @Override
        public void configureRelations(RelationBuilder builder) {
            if (relations != null)
                relations.buildUsingThe(builder);
        }

        @XStreamSerialize
        public List<TypeOfPublicPlacesResource> getResources() {
            return resources;
        }

    }

    @Inject
    private Result                        result;

    @Inject
    private TypesOfPublicPlacesRepository repository;

    @Get
    @Path("/typesofpublicplaces/{entity.id}")
    public void get(TypeOfPublicPlaces entity) {
        result.use(representation()).from(new TypeOfPublicPlacesResource(repository.reload(entity))).serialize();
    }

    @Get
    @Path({ "/typesofpublicplaces?page={page}&max={max}", "/typesofpublicplaces" })
    public void list(Integer page, Integer max) {
        page = page.orWhenNull(0);
        max = max.orWhenNull(10);
        result.use(representation()).from(new TypesOfPublicPlacesResources(repository.list(page, max), pageLinks(page, max))).serialize();
    }

    private BuildRelations pageLinks(final Integer page, final Integer max) {
        return new BuildRelations() {

            @Override
            public void buildUsingThe(RelationBuilder builder) {
                builder.relation("self").uses(TypesOfPublicPlacesController.class).list(page, max);
                builder.relation("next").uses(TypesOfPublicPlacesController.class).list(page + 1, max);
                builder.relation("last").uses(TypesOfPublicPlacesController.class).list(repository.lastPage(max), max);
            }
        };
    }
}
