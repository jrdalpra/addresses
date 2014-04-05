package com.zeroturnaround.rebellabs.addresses.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zeroturnaround.rebellabs.addresses.api.PublicPlacesRepository;
import com.zeroturnaround.rebellabs.addresses.api.exceptions.NotFoundException;
import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.PublicPlace;

public class InMemoryPublicPlacesRepository implements PublicPlacesRepository {

    private Map<Long, PublicPlace> data = new HashMap<>();

    @Override
    public PublicPlace get(Long id) throws NotFoundException {
        if (id == null || !data.containsKey(id))
            throw new NotFoundException();
        return data.get(id);
    }

    @Override
    public PublicPlace reload(PublicPlace entity) throws NotFoundException {
        if (entity == null)
            throw new NotFoundException();
        return get(entity.getId());
    }

    @Override
    public List<PublicPlace> list(int page, int max) {
        return new ArrayList<>(data.values());
    }

    @Override
    public Integer lastPage(int max) {
        return 10;
    }

    @Override
    public List<PublicPlace> listRelatedWith(Locale locale, Integer page, Integer max) {
        List<PublicPlace> relatedWith = new ArrayList<>();
        for (PublicPlace publicPlace : list(page, max))
            if (publicPlace.getLocale().equals(locale))
                relatedWith.add(publicPlace);
        return relatedWith;
    }

    @Override
    public Integer lastPageRelatedWith(Locale locale, Integer max) {
        return 10;
    }

}
