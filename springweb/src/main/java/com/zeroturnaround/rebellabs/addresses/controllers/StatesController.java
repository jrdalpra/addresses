package com.zeroturnaround.rebellabs.addresses.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.inject.Inject;

import lombok.Delegate;
import lombok.NoArgsConstructor;
import lombok.experimental.ExtensionMethod;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zeroturnaround.rebellabs.addresses.FromEntityToResource;
import com.zeroturnaround.rebellabs.addresses.api.StatesRepository;
import com.zeroturnaround.rebellabs.addresses.api.exceptions.NotFoundException;
import com.zeroturnaround.rebellabs.addresses.model.Country;
import com.zeroturnaround.rebellabs.addresses.model.State;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Controller
@ExposesResourceFor(State.class)
@ExtensionMethod({ Numbers.class })
public class StatesController {

    @NoArgsConstructor
    private static class StateResource extends ResourceSupport {

        private static interface ExcludesFromState {
            Country getCountry();

            void setCountry(Country country);
        }

        @Delegate(excludes = { ExcludesFromState.class, ResourceSupport.class })
        private State state;

        public StateResource(State state) {
            this.state = state;
            add(linkTo(methodOn(StatesController.class).get(this.state.getId())).withSelfRel());
            add(linkTo(methodOn(CountriesController.class).get(this.state.getCountry().getId())).withRel("country"));
        }

    }

    private static class FromStateToResource implements ResourceAssembler<State, StateResource> {
        @Override
        public StateResource toResource(State entity) {
            return new StateResource(entity);
        }
    }

    @Inject
    private StatesRepository states;

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/countries/{id}/states", method = GET)
    public ResponseEntity list(@PathVariable("id") Country country,
                               @RequestParam(value = "page", defaultValue = "0") final Integer page,
                               @RequestParam(value = "max", defaultValue = "10") final Integer max) {
        try {
            return new ResponseEntity<>(new Resources<StateResource>(asResourcesFrom(country, page, max)), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Resources<StateResource> asResourcesFrom(Country country, final Integer page, final Integer max) {
        return new Resources<>(new FromEntityToResource<>(states.listWhereCountryEquals(country, page, max), new FromStateToResource()).getResources());
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/states/{id}", method = GET)
    public ResponseEntity get(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(states.get(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
