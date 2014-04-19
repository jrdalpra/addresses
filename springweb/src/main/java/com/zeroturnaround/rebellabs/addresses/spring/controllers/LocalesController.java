package com.zeroturnaround.rebellabs.addresses.spring.controllers;

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

import com.zeroturnaround.rebellabs.addresses.api.LocalesRepository;
import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.Locale.Type;
import com.zeroturnaround.rebellabs.addresses.model.State;
import com.zeroturnaround.rebellabs.addresses.spring.FromEntityListToResourceList;
import com.zeroturnaround.rebellabs.addresses.utils.Numbers;

@Controller
@ExposesResourceFor(Locale.class)
@ExtensionMethod({ Numbers.class })
public class LocalesController {

    @NoArgsConstructor
    private static class LocaleResource extends ResourceSupport {

        private interface ExcludesFromLocale {
            State getState();

            void setState(State state);
        }

        @Delegate(excludes = { ExcludesFromLocale.class, ResourceSupport.class })
        private Locale entity;

        public LocaleResource(Locale entity) {
            this.entity = entity;
            add(linkTo(methodOn(LocalesController.class).get(entity)).withSelfRel());
            add(linkTo(methodOn(StatesController.class).get(entity.getState())).withRel("state"));
            add(linkTo(methodOn(NeighborhoodsController.class).listRelatedWith(entity, 0, 10)).withRel("neighborhoods"));
            add(linkTo(methodOn(PublicPlacesController.class).listRelatedWith(entity, 0, 10)).withRel("publicplaces"));
        }

    }

    private static class LocaleAssembler implements ResourceAssembler<Locale, LocaleResource> {

        @Override
        public LocaleResource toResource(Locale entity) {
            return new LocaleResource(entity);
        }

    }

    @Inject
    private LocalesRepository locales;

    @RequestMapping("/locales/{id}")
    public ResponseEntity<LocaleResource> get(@PathVariable("id") Locale locale) {
        return new ResponseEntity<LocalesController.LocaleResource>(new LocaleResource(locales.reload(locale)), HttpStatus.OK);
    }

    @RequestMapping("/locales")
    public ResponseEntity<Resources<LocaleResource>> list(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                          @RequestParam(value = "max", defaultValue = "10") Integer max) {
        return new ResponseEntity<>(new Resources<>(ofLocales(page, max), pageLinks(page, max)), HttpStatus.OK);
    }

    private List<LocaleResource> ofLocales(Integer page, Integer max) {
        return listOfResourcesFrom(locales.list(page, max));
    }

    private List<Link> pageLinks(Integer page, Integer max) {
        return asList(linkTo(methodOn(LocalesController.class).list(0, max)).withRel("first"),
                      linkTo(methodOn(LocalesController.class).list(page.orWhenNull(0) + 1, max)).withRel("next"),
                      linkTo(methodOn(LocalesController.class).list(locales.lastPage(max), max)).withRel("last"));
    }

    @RequestMapping("/states/{id}/locales")
    public ResponseEntity<Resources<LocaleResource>> listByStateAndType(@PathVariable("id") State state,
                                                                        @RequestParam("type") Locale.Type type,
                                                                        @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                        @RequestParam(value = "max", defaultValue = "10") Integer max) {
        return new ResponseEntity<>(new Resources<>(ofLocalesFrom(state, type, page, max), pageLinksBy(state, type, page, max)), HttpStatus.OK);
    }

    private List<LocaleResource> ofLocalesFrom(State state, Locale.Type type, int page, int max) {
        return listOfResourcesFrom(locales.listByStateAndType(state, type, page, max));
    }

    private Iterable<Link> pageLinksBy(State state, Type type, Integer page, Integer max) {
        return asList(linkTo(methodOn(LocalesController.class).listByStateAndType(state, type, 0, max)).withRel("first"),
                      linkTo(methodOn(LocalesController.class).listByStateAndType(state, type, page.orWhenNull(0) + 1, max)).withRel("next"),
                      linkTo(methodOn(LocalesController.class).listByStateAndType(state, type, locales.lastPage(state, type, max), max)).withRel("last"));
    }

    private List<LocaleResource> listOfResourcesFrom(List<Locale> list) {
        return new FromEntityListToResourceList<>(list, new LocaleAssembler()).getResources();
    }

}
