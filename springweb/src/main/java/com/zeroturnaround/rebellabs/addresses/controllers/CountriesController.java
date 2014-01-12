package com.zeroturnaround.rebellabs.addresses.controllers;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.inject.Inject;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zeroturnaround.rebellabs.addresses.api.CountriesRepository;
import com.zeroturnaround.rebellabs.addresses.api.exceptions.NotFoundException;
import com.zeroturnaround.rebellabs.addresses.model.Country;

@Controller
@ExposesResourceFor(Country.class)
@RequestMapping("/countries")
public class CountriesController {

    @Inject
    private CountriesRepository countries;

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/{id}", method = GET)
    public ResponseEntity get(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(countries.get(id), HttpStatus.OK);
        } catch (NotFoundException notFound) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
