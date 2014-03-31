package com.zeroturnaround.rebellabs.addresses.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import javax.inject.Inject;

import lombok.experimental.ExtensionMethod;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zeroturnaround.rebellabs.addresses.FromEntityListToResourceList;
import com.zeroturnaround.rebellabs.addresses.api.CountriesRepository;
import com.zeroturnaround.rebellabs.addresses.api.exceptions.NotFoundException;
import com.zeroturnaround.rebellabs.addresses.model.Country;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Controller
@ExposesResourceFor(Country.class)
@RequestMapping("/countries")
@ExtensionMethod({ Numbers.class })
public class CountriesController {

    private static class FromCountryToResource implements ResourceAssembler<Country, Resource<Country>> {
        @Override
        public Resource<Country> toResource(Country entity) {
            return new Resource<Country>(entity,
                                         linkTo(methodOn(CountriesController.class).get(entity.getId())).withSelfRel(),
                                         linkTo(methodOn(StatesController.class).listByCountry(entity, 0, 10)).withRel("states"));
        }
    }

    @Inject
    private CountriesRepository countries;

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/", method = GET)
    public ResponseEntity list(@RequestParam(value = "page", defaultValue = "0") final Integer page,
                               @RequestParam(value = "max", defaultValue = "10") final Integer max) {
        try {
            return new ResponseEntity<Resources<Resource<Country>>>(page(page, max), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Resources<Resource<Country>> page(final Integer page, final Integer max) {
        return new Resources<>(from(page, max),
                               linkTo(methodOn(CountriesController.class).list(0, max)).withRel("first"),
                               linkTo(methodOn(CountriesController.class).list(page.orWhenNull(0) + 1, max)).withRel("next"),
                               linkTo(methodOn(CountriesController.class).list(countries.lastPage(max), max)).withRel("last"));
    }

    private List<Resource<Country>> from(Integer page, Integer max) {
        return new FromEntityListToResourceList<Country, Resource<Country>>(countries.list(page.orWhenNull(0), max.orWhenNull(10)), new FromCountryToResource()).getResources();
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/{id}", method = GET)
    public ResponseEntity get(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<Resource<Country>>(new FromCountryToResource().toResource(countries.get(id)), HttpStatus.OK);
        } catch (NotFoundException notFound) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
