package com.zeroturnaround.rebellabs.addresses.controllers;

import static java.util.Arrays.asList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import javax.inject.Inject;

import lombok.Delegate;
import lombok.NoArgsConstructor;
import lombok.experimental.ExtensionMethod;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zeroturnaround.rebellabs.addresses.FromEntityListToResourceList;
import com.zeroturnaround.rebellabs.addresses.api.TypesOfPublicPlacesRepository;
import com.zeroturnaround.rebellabs.addresses.model.TypeOfPublicPlaces;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Controller
@ExposesResourceFor(TypeOfPublicPlaces.class)
@ExtensionMethod({ Numbers.class })
public class TypesOfPublicPlacesController {

    @NoArgsConstructor
    public static class TypeOfPublicPlacesResource extends ResourceSupport {

        @Delegate(excludes = { ResourceSupport.class })
        private TypeOfPublicPlaces entity;

        public TypeOfPublicPlacesResource(TypeOfPublicPlaces entity) {
            this.entity = entity;
            add(linkTo(methodOn(TypesOfPublicPlacesController.class).get(entity)).withSelfRel());
        }

    }

    public static class TypeOfPublicPlacesAssembler implements ResourceAssembler<TypeOfPublicPlaces, TypeOfPublicPlacesResource> {

        @Override
        public TypeOfPublicPlacesResource toResource(TypeOfPublicPlaces entity) {
            return new TypeOfPublicPlacesResource(entity);
        }

    }

    @Inject
    private TypesOfPublicPlacesRepository types;

    @RequestMapping("/typesofpublicplaces/{id}")
    public ResponseEntity<TypeOfPublicPlacesResource> get(@PathVariable("id") TypeOfPublicPlaces type) {
        return new ResponseEntity<>(new TypeOfPublicPlacesResource(types.reload(type)), HttpStatus.OK);
    }

    @RequestMapping("/typesofpublicplaces")
    public ResponseEntity<Resources<TypeOfPublicPlacesResource>> list(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                      @RequestParam(value = "max", defaultValue = "10") Integer max) {
        return new ResponseEntity<>(new Resources<>(ofTypesOfPublicPlaces(page, max), withPageLinks(page, max)), HttpStatus.OK);
    }

    private List<TypeOfPublicPlacesResource> ofTypesOfPublicPlaces(Integer page, Integer max) {
        return listOfResourcesFrom(types.list(page, max));
    }

    private List<TypeOfPublicPlacesResource> listOfResourcesFrom(List<TypeOfPublicPlaces> list) {
        return new FromEntityListToResourceList<>(list, new TypeOfPublicPlacesAssembler()).getResources();
    }

    private Iterable<Link> withPageLinks(Integer page, Integer max) {
        return asList(linkTo(methodOn(TypesOfPublicPlacesController.class).list(0, max)).withRel("first"),
                      linkTo(methodOn(TypesOfPublicPlacesController.class).list(page.orWhenNull(0) + 1, max)).withRel("next"),
                      linkTo(methodOn(TypesOfPublicPlacesController.class).list(types.lastPage(max), max)).withRel("last"));

    }
}
