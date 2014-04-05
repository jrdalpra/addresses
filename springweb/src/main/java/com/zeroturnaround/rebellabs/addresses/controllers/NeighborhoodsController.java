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
import com.zeroturnaround.rebellabs.addresses.api.NeighborhoodsRepository;
import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.Neighborhood;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Controller
@ExposesResourceFor(Neighborhood.class)
@ExtensionMethod({ Numbers.class })
public class NeighborhoodsController {

    @NoArgsConstructor
    public static class NeighborhoodResource extends ResourceSupport {

        private interface Excludes {
            Locale getLocale();

            void setLocale(Locale locale);
        }

        @Delegate(excludes = { Excludes.class, ResourceSupport.class })
        private Neighborhood entity;

        public NeighborhoodResource(Neighborhood entity) {
            this.entity = entity;
            add(linkTo(methodOn(NeighborhoodsController.class).get(entity)).withSelfRel());
            add(linkTo(methodOn(LocalesController.class).get(entity.getLocale())).withRel("locale"));
        }
    }

    public static class NeighborhoodAssembler implements ResourceAssembler<Neighborhood, NeighborhoodResource> {
        @Override
        public NeighborhoodResource toResource(Neighborhood entity) {
            return new NeighborhoodResource(entity);
        }
    }

    @Inject
    private NeighborhoodsRepository neighborhoods;

    @RequestMapping("/neighborhoods/{id}")
    public ResponseEntity<NeighborhoodResource> get(@PathVariable("id") Neighborhood neighborhood) {
        return new ResponseEntity<>(new NeighborhoodResource(neighborhoods.reload(neighborhood)), HttpStatus.OK);
    }

    @RequestMapping("/neighborhoods")
    public ResponseEntity<Resources<NeighborhoodResource>> list(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                @RequestParam(value = "max", defaultValue = "10") Integer max) {
        return new ResponseEntity<>(new Resources<>(ofNeighborhoods(page, max), withPageLinks(page, max)), HttpStatus.OK);
    }

    private List<NeighborhoodResource> ofNeighborhoods(Integer page, Integer max) {
        return listOfResourcesFrom(neighborhoods.list(page, max));
    }

    private List<Link> withPageLinks(Integer page, Integer max) {
        return asList(linkTo(methodOn(NeighborhoodsController.class).list(0, max)).withRel("first"),
                      linkTo(methodOn(NeighborhoodsController.class).list(page.orWhenNull(0) + 1, max)).withRel("next"),
                      linkTo(methodOn(NeighborhoodsController.class).list(neighborhoods.lastPage(max), max)).withRel("last"));
    }

    @RequestMapping("/locales/{id}/neighborhoods")
    public ResponseEntity<Resources<NeighborhoodResource>> listRelatedWith(@PathVariable("id") Locale locale,
                                                                           @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                           @RequestParam(value = "max", defaultValue = "10") Integer max) {
        return new ResponseEntity<>(new Resources<>(ofNeighborhoodsRelatedWith(locale, page, max), pageLinksFrom(locale, page, max)), HttpStatus.OK);
    }

    private List<NeighborhoodResource> ofNeighborhoodsRelatedWith(Locale locale, Integer page, Integer max) {
        return listOfResourcesFrom(neighborhoods.listRelatedWith(locale, page, max));
    }

    private List<Link> pageLinksFrom(Locale locale, Integer page, Integer max) {
        return asList(linkTo(methodOn(NeighborhoodsController.class).listRelatedWith(locale, 0, max)).withRel("first"),
                      linkTo(methodOn(NeighborhoodsController.class).listRelatedWith(locale, page.orWhenNull(0) + 1, max)).withRel("next"),
                      linkTo(methodOn(NeighborhoodsController.class).listRelatedWith(locale, neighborhoods.lastPageRelatedWith(locale, max), max)).withRel("last"));
    }

    private List<NeighborhoodResource> listOfResourcesFrom(List<Neighborhood> list) {
        return new FromEntityListToResourceList<>(list, new NeighborhoodAssembler()).getResources();
    }
}
