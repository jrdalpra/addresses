package com.zeroturnaround.rebellabs.addresses.vraptor.infra;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.ioc.guice.GuiceProvider;

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

public class CustomGuiceProvider extends GuiceProvider {

    @Override
    protected void registerCustomComponents(ComponentRegistry registry) {
        super.registerCustomComponents(registry);
        registry.register(CountriesRepository.class, InMemoryCountriesRepository.class);
        registry.register(LocalesRepository.class, InMemoryLocalesRepository.class);
        registry.register(NeighborhoodsRepository.class, InMemoryNeighborhoodsRepository.class);
        registry.register(PublicPlacesRepository.class, InMemoryPublicPlacesRepository.class);
        registry.register(StatesRepository.class, InMemoryStatesRepository.class);
        registry.register(TypesOfPublicPlacesRepository.class, InMemoryTypesOfPublicPlacesRepository.class);
    }

}
