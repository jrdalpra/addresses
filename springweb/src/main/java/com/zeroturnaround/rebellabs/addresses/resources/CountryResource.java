package com.zeroturnaround.rebellabs.addresses.resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import lombok.Getter;

import org.springframework.hateoas.ResourceSupport;

import com.zeroturnaround.rebellabs.addresses.controllers.CountriesController;
import com.zeroturnaround.rebellabs.addresses.controllers.StatesController;
import com.zeroturnaround.rebellabs.addresses.model.Country;

@Getter
public class CountryResource extends ResourceSupport {

    private final Country self;

    public CountryResource(Country self) {
        super();
        this.self = self;
        add(linkTo(methodOn(CountriesController.class).get(this.self.getId())).withSelfRel());
        add(linkTo(methodOn(StatesController.class, self.getId()).list(this.self, null, null)).withRel("states"));
    }

}
