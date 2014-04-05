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
import com.zeroturnaround.rebellabs.addresses.api.PublicPlacesRepository;
import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.Neighborhood;
import com.zeroturnaround.rebellabs.addresses.model.PublicPlace;
import com.zeroturnaround.rebellabs.addresses.model.TypeOfPublicPlaces;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Controller
@ExposesResourceFor(PublicPlace.class)
@ExtensionMethod({ Numbers.class })
public class PublicPlacesController {

    @NoArgsConstructor
    public static class PublicPlaceResource extends ResourceSupport {

        private interface ExcludesFromPublicPlace {
            void setLocale(Locale locale);

            Locale getLocale();

            void setType(TypeOfPublicPlaces type);

            TypeOfPublicPlaces getType();

            void setNeighborhood(Neighborhood neighborhood);

            Neighborhood getNeighborhood();
        }

        @Delegate(excludes = { ExcludesFromPublicPlace.class, ResourceSupport.class })
        private PublicPlace entity;

        public PublicPlaceResource(PublicPlace entity) {
            this.entity = entity;
            add(linkTo(methodOn(PublicPlacesController.class).get(entity)).withSelfRel());
            add(linkTo(methodOn(LocalesController.class).get(entity.getLocale())).withRel("locale"));
            add(linkTo(methodOn(TypesOfPublicPlacesController.class).get(entity.getType())).withRel("type"));
            add(linkTo(methodOn(NeighborhoodsController.class).get(entity.getNeighborhood())).withRel("neighborhood"));
        }

    }

    @NoArgsConstructor
    public static class PublicPlaceAssembler implements ResourceAssembler<PublicPlace, PublicPlaceResource> {
        @Override
        public PublicPlaceResource toResource(PublicPlace entity) {
            return new PublicPlaceResource(entity);
        }
    }

    @Inject
    private PublicPlacesRepository publicPlaces;

    @RequestMapping("/publicplaces/{id}")
    public ResponseEntity<PublicPlaceResource> get(@PathVariable("id") PublicPlace publicPlace) {
        return new ResponseEntity<>(new PublicPlaceResource(publicPlaces.reload(publicPlace)), HttpStatus.OK);
    }

    @RequestMapping("/publicplaces")
    public ResponseEntity<Resources<PublicPlaceResource>> list(@RequestParam(value = "page", defaultValue = "0") final Integer page,
                                                               @RequestParam(value = "max", defaultValue = "10") final Integer max) {
        return new ResponseEntity<>(new Resources<>(ofPublicPlaces(page, max), withPageLinks(page, max)), HttpStatus.OK);

    }

    private List<PublicPlaceResource> ofPublicPlaces(final Integer page, final Integer max) {
        return listOfResourcesFrom(publicPlaces.list(page, max));
    }

    private Iterable<Link> withPageLinks(Integer page, Integer max) {
        return asList(linkTo(methodOn(PublicPlacesController.class).list(0, max)).withRel("first"),
                      linkTo(methodOn(PublicPlacesController.class).list(page.orWhenNull(0) + 1, max)).withRel("next"),
                      linkTo(methodOn(PublicPlacesController.class).list(publicPlaces.lastPage(max), max)).withRel("last"));
    }

    @RequestMapping("/locales/{id}/publicplaces")
    public ResponseEntity<Resources<PublicPlaceResource>> listRelatedWith(@PathVariable("id") Locale locale,
                                                                          @RequestParam(value = "page", defaultValue = "0") final Integer page,
                                                                          @RequestParam(value = "max", defaultValue = "10") final Integer max) {
        return new ResponseEntity<>(new Resources<>(ofPublicPlacesRelatedWith(locale, page, max), withPageLinksRelatedWith(locale, page, max)), HttpStatus.OK);

    }

    private List<PublicPlaceResource> ofPublicPlacesRelatedWith(Locale locale, Integer page, Integer max) {
        return listOfResourcesFrom(publicPlaces.listRelatedWith(locale, page, max));
    }

    private Iterable<Link> withPageLinksRelatedWith(Locale locale, Integer page, Integer max) {
        return asList(linkTo(methodOn(PublicPlacesController.class).listRelatedWith(locale, 0, max)).withRel("first"),
                      linkTo(methodOn(PublicPlacesController.class).listRelatedWith(locale, page.orWhenNull(0) + 1, max)).withRel("next"),
                      linkTo(methodOn(PublicPlacesController.class).listRelatedWith(locale, publicPlaces.lastPageRelatedWith(locale, max), max)).withRel("last"));
    }

    private List<PublicPlaceResource> listOfResourcesFrom(List<PublicPlace> list) {
        return new FromEntityListToResourceList<PublicPlace, PublicPlaceResource>(list, new PublicPlaceAssembler()).getResources();
    }

}
