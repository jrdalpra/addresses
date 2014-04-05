package com.zeroturnaround.rebellabs.addresses.controllers;

import static java.util.Arrays.asList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

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
import com.zeroturnaround.rebellabs.addresses.api.CountriesRepository;
import com.zeroturnaround.rebellabs.addresses.model.Country;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Controller
@ExposesResourceFor(Country.class)
@RequestMapping("/countries")
@ExtensionMethod({ Numbers.class })
public class CountriesController {

    public static class CountryResource extends ResourceSupport {

        @Delegate(excludes = { ResourceSupport.class })
        private Country entity;

        public CountryResource(Country entity) {
            this.entity = entity;
            add(linkTo(methodOn(CountriesController.class).get(entity)).withSelfRel());
            add(linkTo(methodOn(StatesController.class).listByCountry(entity, 0, 10)).withRel("states"));
        }

    }

    @NoArgsConstructor
    private static class CountryAssembler implements ResourceAssembler<Country, CountryResource> {
        @Override
        public CountryResource toResource(Country entity) {
            return new CountryResource(entity);
        }
    }

    @Inject
    private CountriesRepository countries;

    @RequestMapping(value = "/{id}", method = GET)
    public ResponseEntity<CountryResource> get(@PathVariable("id") Country country) {
        return new ResponseEntity<CountryResource>(new CountryResource(countries.reload(country)), HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = GET)
    public ResponseEntity<Resources<CountryResource>> list(@RequestParam(value = "page", defaultValue = "0") final Integer page,
                                                           @RequestParam(value = "max", defaultValue = "10") final Integer max) {
        return new ResponseEntity<>(new Resources<>(ofCountries(page, max), withPageLinks(page, max)), HttpStatus.OK);
    }

    private List<CountryResource> ofCountries(Integer page, Integer max) {
        return listOfResourcesFrom(countries.list(page.orWhenNull(0), max.orWhenNull(10)));
    }

    private List<Link> withPageLinks(Integer page, Integer max) {
        return asList(linkTo(methodOn(CountriesController.class).list(0, max)).withRel("first"),
                      linkTo(methodOn(CountriesController.class).list(page.orWhenNull(0) + 1, max)).withRel("next"),
                      linkTo(methodOn(CountriesController.class).list(countries.lastPage(max), max)).withRel("last"));
    }

    private List<CountryResource> listOfResourcesFrom(List<Country> list) {
        return new FromEntityListToResourceList<Country, CountryResource>(list, new CountryAssembler()).getResources();
    }

}
