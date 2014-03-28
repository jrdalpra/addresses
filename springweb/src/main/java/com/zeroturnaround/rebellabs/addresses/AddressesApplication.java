package com.zeroturnaround.rebellabs.addresses;

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
import com.zeroturnaround.rebellabs.addresses.api.InMemoryStateRepository;
import com.zeroturnaround.rebellabs.addresses.api.StatesRepository;

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
            return new InMemoryStateRepository();
        }

    }

}
