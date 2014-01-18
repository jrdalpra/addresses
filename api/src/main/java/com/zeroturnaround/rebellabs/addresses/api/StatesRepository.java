package com.zeroturnaround.rebellabs.addresses.api;

import java.util.List;

import com.zeroturnaround.rebellabs.addresses.model.Country;
import com.zeroturnaround.rebellabs.addresses.model.State;

public interface StatesRepository extends CommonsMethodsForARepository<State, Long> {

    List<State> listWhereCountryEquals(Country localized, int page, int max);

}
