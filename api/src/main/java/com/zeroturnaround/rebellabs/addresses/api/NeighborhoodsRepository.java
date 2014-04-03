package com.zeroturnaround.rebellabs.addresses.api;

import java.util.List;

import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.Neighborhood;

public interface NeighborhoodsRepository extends CommonsMethodsForARepository<Neighborhood, Long> {

    List<Neighborhood> listRelatedWith(Locale locale, Integer page, Integer max);

    Integer lastPageRelatedWith(Locale locale, Integer max);

}
