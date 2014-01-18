package com.zeroturnaround.rebellabs.addresses.controllers;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import javax.inject.Inject;

import lombok.experimental.ExtensionMethod;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zeroturnaround.rebellabs.addresses.FromEntityToResource;
import com.zeroturnaround.rebellabs.addresses.api.StatesRepository;
import com.zeroturnaround.rebellabs.addresses.model.Country;
import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.State;
import com.zeroturnaround.rebellabs.addresses.resources.PageResource;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Controller
@ExposesResourceFor(Locale.class)
@RequestMapping("/countries/{id}")
@ExtensionMethod({ Numbers.class })
public class StatesController {

    @Inject
    private StatesRepository states;

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/", method = GET)
    public ResponseEntity list(@PathVariable("id") Country country,
                               @RequestParam(value = "page", defaultValue = "0") final Integer page,
                               @RequestParam(value = "max", defaultValue = "10") final Integer max) {
        return new ResponseEntity<>(new PageResource<>(listAsResources(country, page, max)), HttpStatus.OK);
    }

    private List<Resource<State>> listAsResources(Country country, final Integer page, final Integer max) {
        return new FromEntityToResource<>(states.listWhereCountryEquals(country, page, max)).getResources();
    }

}
