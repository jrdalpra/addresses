package com.zeroturnaround.rebellabs.addresses.api;

import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.zeroturnaround.rebellabs.addresses.api.exceptions.NotFoundException;
import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.Neighborhood;
import com.zeroturnaround.rebellabs.addresses.model.PublicPlace;
import com.zeroturnaround.rebellabs.addresses.model.TypeOfPublicPlaces;

public class InMemoryPublicPlacesRepository implements PublicPlacesRepository {

    private Map<Long, PublicPlace>        data = new HashMap<>();

    @Inject
    private LocalesRepository             locales;

    @Inject
    private NeighborhoodsRepository       neighborhoods;

    @Inject
    private TypesOfPublicPlacesRepository types;

    @PostConstruct
    public void setup() {
        Long id = 0l;
        for (Locale locale : locales.list(0, 99999)) {
            for (Neighborhood neighborhood : neighborhoods.listRelatedWith(locale, 0, 99999)) {
                for (TypeOfPublicPlaces type : types.list(0, 99999)) {
                    id++;
                    data.put(id, new PublicPlace(id, "Public Place " + id, type, locale, neighborhood));
                }
            }
        }
    }

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
        ArrayList<PublicPlace> all = new ArrayList<>(data.values());
        return all.subList(page, min(max, all.size() - 1));
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
