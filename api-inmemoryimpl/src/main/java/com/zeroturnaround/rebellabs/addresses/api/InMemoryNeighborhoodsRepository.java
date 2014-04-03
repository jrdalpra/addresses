package com.zeroturnaround.rebellabs.addresses.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.zeroturnaround.rebellabs.addresses.api.exceptions.NotFoundException;
import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.Neighborhood;

public class InMemoryNeighborhoodsRepository implements NeighborhoodsRepository {

    @Inject
    private LocalesRepository       locales;

    private Map<Long, Neighborhood> data = new HashMap<>();

    @PostConstruct
    public void setup() {
        Long id = 0l;
        for (Locale locale : locales.list(0, 999))
            for (int i = 0; i < 10; i++) {
                id++;
                data.put(id, new Neighborhood(id, "Neighborhood " + id, locale));
            }
    }

    @Override
    public Neighborhood get(Long id) throws NotFoundException {
        if (id == null || !data.containsKey(id))
            throw new NotFoundException();
        return data.get(id);
    }

    @Override
    public Neighborhood reload(Neighborhood entity) throws NotFoundException {
        if (entity == null)
            throw new NotFoundException();
        return get(entity.getId());
    }

    @Override
    public List<Neighborhood> list(int page, int max) {
        return new ArrayList<>(data.values());
    }

    @Override
    public Integer lastPage(int max) {
        return 10;
    }

    @Override
    public List<Neighborhood> listRelatedWith(Locale locale, Integer page, Integer max) {
        List<Neighborhood> relatedWith = new ArrayList<>();
        for (Neighborhood neighborhood : list(page, max))
            if (neighborhood.getLocale().equals(locale))
                relatedWith.add(neighborhood);
        return relatedWith;
    }

    @Override
    public Integer lastPageRelatedWith(Locale locale, Integer max) {
        return 10;
    }

}
