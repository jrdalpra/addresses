package com.zeroturnaround.rebellabs.addresses.api;

import java.util.List;

import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.PublicPlace;

public interface PublicPlacesRepository extends CommonsMethodsForARepository<PublicPlace, Long> {

    List<PublicPlace> listRelatedWith(Locale locale, Integer page, Integer max);

    Integer lastPageRelatedWith(Locale locale, Integer max);

}
