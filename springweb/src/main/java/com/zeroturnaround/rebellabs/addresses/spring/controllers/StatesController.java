package com.zeroturnaround.rebellabs.addresses.spring.controllers;

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

import com.zeroturnaround.rebellabs.addresses.api.StatesRepository;
import com.zeroturnaround.rebellabs.addresses.model.Country;
import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.State;
import com.zeroturnaround.rebellabs.addresses.spring.FromEntityListToResourceList;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Controller
@ExposesResourceFor(State.class)
@ExtensionMethod({ Numbers.class })
public class StatesController {

    public static class StateResource extends ResourceSupport {

        private static interface ExcludesFromState {
            Country getCountry();

            void setCountry(Country country);
        }

        @Delegate(excludes = { ExcludesFromState.class, ResourceSupport.class })
        private State state;

        public StateResource(State state) {
            this.state = state;
            add(linkTo(methodOn(StatesController.class).get(state)).withSelfRel());
            add(linkTo(methodOn(CountriesController.class).get(state.getCountry())).withRel("country"));
            add(linkTo(methodOn(LocalesController.class).listByStateAndType(state, Locale.Type.CITY, 0, 10)).withRel("cities"));
            add(linkTo(methodOn(LocalesController.class).listByStateAndType(state, Locale.Type.DISTRICT, 0, 10)).withRel("districts"));
            add(linkTo(methodOn(LocalesController.class).listByStateAndType(state, Locale.Type.VILLAGE, 0, 10)).withRel("villages"));
        }

    }

    @NoArgsConstructor
    private static class StateResourceAssembler implements ResourceAssembler<State, StateResource> {
        @Override
        public StateResource toResource(State entity) {
            return new StateResource(entity);
        }
    }

    @Inject
    private StatesRepository states;

    @RequestMapping(value = "/states/{id}", method = GET)
    public ResponseEntity<StateResource> get(@PathVariable("id") State state) {
        return new ResponseEntity<>(new StateResource(states.reload(state)), HttpStatus.OK);
    }

    @RequestMapping(value = "/countries/{id}/states", method = GET)
    public ResponseEntity<Resources<StateResource>> listByCountry(@PathVariable("id") Country country,
                                                                  @RequestParam(value = "page", defaultValue = "0") final Integer page,
                                                                  @RequestParam(value = "max", defaultValue = "10") final Integer max) {
        return new ResponseEntity<>(new Resources<>(ofStatesFrom(country, page, max), withPageLinksBy(country, page, max)), HttpStatus.OK);
    }

    private Iterable<Link> withPageLinksBy(Country country, Integer page, Integer max) {
        return asList(linkTo(methodOn(StatesController.class).listByCountry(country, 0, max)).withRel("first"),
                      linkTo(methodOn(StatesController.class).listByCountry(country, page.orWhenNull(0) + 1, max)).withRel("next"),
                      linkTo(methodOn(StatesController.class).listByCountry(country, states.lastPage(country, max), max)).withRel("last"));
    }

    private List<StateResource> ofStatesFrom(Country country, final Integer page, final Integer max) {
        return listOfResourcesFrom(states.listWhereCountryEquals(country, page, max));
    }

    @RequestMapping(value = "/states", method = GET)
    public ResponseEntity<Resources<StateResource>> list(@RequestParam(value = "page", defaultValue = "0") final Integer page,
                                                         @RequestParam(value = "max", defaultValue = "10") final Integer max) {
        return new ResponseEntity<>(new Resources<>(ofStates(page, max), withPageLinks(page, max)), HttpStatus.OK);
    }

    private List<StateResource> ofStates(final Integer page, final Integer max) {
        return listOfResourcesFrom(states.list(page, max));
    }

    private Iterable<Link> withPageLinks(Integer page, Integer max) {
        return asList(linkTo(methodOn(StatesController.class).list(0, max)).withRel("first"),
                      linkTo(methodOn(StatesController.class).list(page.orWhenNull(0) + 1, max)).withRel("next"),
                      linkTo(methodOn(StatesController.class).list(states.lastPage(max), max)).withRel("last"));
    }

    private List<StateResource> listOfResourcesFrom(List<State> listWhereCountryEquals) {
        return new FromEntityListToResourceList<>(listWhereCountryEquals, new StateResourceAssembler()).getResources();
    }

}
