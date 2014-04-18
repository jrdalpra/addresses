package com.zeroturnaround.rebellabs.addresses.jaxrs.infra;

import javax.enterprise.inject.Produces;

import com.zeroturnaround.rebellabs.addresses.api.CountriesRepository;
import com.zeroturnaround.rebellabs.addresses.api.InMemoryCountriesRepository;
import com.zeroturnaround.rebellabs.addresses.api.InMemoryLocalesRepository;
import com.zeroturnaround.rebellabs.addresses.api.InMemoryNeighborhoodsRepository;
import com.zeroturnaround.rebellabs.addresses.api.InMemoryPublicPlacesRepository;
import com.zeroturnaround.rebellabs.addresses.api.InMemoryStatesRepository;
import com.zeroturnaround.rebellabs.addresses.api.InMemoryTypesOfPublicPlacesRepository;
import com.zeroturnaround.rebellabs.addresses.api.LocalesRepository;
import com.zeroturnaround.rebellabs.addresses.api.NeighborhoodsRepository;
import com.zeroturnaround.rebellabs.addresses.api.PublicPlacesRepository;
import com.zeroturnaround.rebellabs.addresses.api.StatesRepository;
import com.zeroturnaround.rebellabs.addresses.api.TypesOfPublicPlacesRepository;

public class RepositoriesFactory {

    @Produces
    public CountriesRepository countriesRepository() {
        return new InMemoryCountriesRepository();
    }

    @Produces
    public LocalesRepository localesRepository() {
        return new InMemoryLocalesRepository();
    }

    @Produces
    public NeighborhoodsRepository neighborhoodsRepository() {
        return new InMemoryNeighborhoodsRepository();
    }

    @Produces
    public PublicPlacesRepository publicPlacesRepository() {
        return new InMemoryPublicPlacesRepository();
    }

    @Produces
    public StatesRepository statesRepository() {
        return new InMemoryStatesRepository();
    }

    @Produces
    public TypesOfPublicPlacesRepository typesOfPublicPlacesRepository() {
        return new InMemoryTypesOfPublicPlacesRepository();
    }
}
