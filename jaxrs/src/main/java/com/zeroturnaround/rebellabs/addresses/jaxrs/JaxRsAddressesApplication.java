package com.zeroturnaround.rebellabs.addresses.jaxrs;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.ApplicationPath;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jettison.JettisonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.zeroturnaround.rebellabs.addresses.api.CountriesRepository;
import com.zeroturnaround.rebellabs.addresses.api.LocalesRepository;
import com.zeroturnaround.rebellabs.addresses.api.NeighborhoodsRepository;
import com.zeroturnaround.rebellabs.addresses.api.PublicPlacesRepository;
import com.zeroturnaround.rebellabs.addresses.api.StatesRepository;
import com.zeroturnaround.rebellabs.addresses.api.TypesOfPublicPlacesRepository;

@ApplicationPath("api")
public class JaxRsAddressesApplication extends ResourceConfig {

    private static class CdiBinder extends AbstractBinder {

        @Override
        protected void configure() {
            bind(a_CDI_BeanOf(CountriesRepository.class)).to(CountriesRepository.class);
            bind(a_CDI_BeanOf(LocalesRepository.class)).to(LocalesRepository.class);
            bind(a_CDI_BeanOf(NeighborhoodsRepository.class)).to(NeighborhoodsRepository.class);
            bind(a_CDI_BeanOf(PublicPlacesRepository.class)).to(PublicPlacesRepository.class);
            bind(a_CDI_BeanOf(StatesRepository.class)).to(StatesRepository.class);
            bind(a_CDI_BeanOf(TypesOfPublicPlacesRepository.class)).to(TypesOfPublicPlacesRepository.class);
        }

        @SuppressWarnings("unchecked")
        private <T> T a_CDI_BeanOf(Class<T> type) {
            BeanManager bm = CDI.current().getBeanManager();
            Bean<T> bean = (Bean<T>) bm.getBeans(type).iterator().next();
            CreationalContext<T> ctx = bm.createCreationalContext(bean);
            return (T) bm.getReference(bean, type, ctx);
        }

    }

    public JaxRsAddressesApplication() {
        packages("com.zeroturnaround.rebellabs.addresses.jaxrs.controllers");
        register(new CdiBinder());
        register(JettisonFeature.class);
    }
}
