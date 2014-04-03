package com.zeroturnaround.rebellabs.addresses.controllers;

import javax.inject.Inject;

import lombok.Delegate;
import lombok.experimental.ExtensionMethod;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zeroturnaround.rebellabs.addresses.api.TypesOfPublicPlacesRepository;
import com.zeroturnaround.rebellabs.addresses.model.TypeOfPublicPlaces;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Controller
@ExposesResourceFor(TypeOfPublicPlaces.class)
@ExtensionMethod({ Numbers.class })
public class TypesOfPublicPlacesController {

    public static class TypeOfPublicPlacesResource extends ResourceSupport {

        @Delegate(excludes = { ResourceSupport.class })
        public TypeOfPublicPlaces entity;

        public TypeOfPublicPlacesResource(TypeOfPublicPlaces entity) {
            this.entity = entity;
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

}
