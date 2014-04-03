package com.zeroturnaround.rebellabs.addresses.api;

import java.util.List;

import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.Locale.Type;
import com.zeroturnaround.rebellabs.addresses.model.State;

public interface LocalesRepository extends CommonsMethodsForARepository<Locale, Long> {

    List<Locale> listByStateAndType(State state, Type type, int page, int max);

    Integer lastPage(State state, Type type, Integer max);

}
