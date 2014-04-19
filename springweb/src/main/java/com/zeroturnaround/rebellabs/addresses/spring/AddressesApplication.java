package com.zeroturnaround.rebellabs.addresses.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

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

public class AddressesApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AddressesConfiguration.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AddressesConfiguration.class);
    }

    @Configuration
    @EnableAutoConfiguration
    @ComponentScan(excludeFilters = @Filter({ Service.class, Configuration.class }))
    static class AddressesConfiguration {

        @Bean
        public CountriesRepository countriesRepository() {
            return new InMemoryCountriesRepository();
        }

        @Bean
        public StatesRepository statesRepository() {
            return new InMemoryStatesRepository();
        }

        @Bean
        public LocalesRepository localesRepository() {
            return new InMemoryLocalesRepository();
        }

        @Bean
        public TypesOfPublicPlacesRepository typesOfPublicPlacesRepository() {
            return new InMemoryTypesOfPublicPlacesRepository();
        }

        @Bean
        public NeighborhoodsRepository neighborhoodsRepository() {
            return new InMemoryNeighborhoodsRepository();
        }

        @Bean
        public PublicPlacesRepository publicPlacesRepository() {
            return new InMemoryPublicPlacesRepository();
        }

    }

}
