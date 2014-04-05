package com.zeroturnaround.rebellabs.addresses.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.zeroturnaround.rebellabs.addresses.api.exceptions.NotFoundException;
import com.zeroturnaround.rebellabs.addresses.model.TypeOfPublicPlaces;

public class InMemoryTypesOfPublicPlacesRepository implements TypesOfPublicPlacesRepository {

    private Map<Long, TypeOfPublicPlaces> data = new HashMap<>();

    @PostConstruct
    public void setup() {
        data.put(1l, new TypeOfPublicPlaces(1l, "Street"));
        data.put(2l, new TypeOfPublicPlaces(2l, "Avenue"));
        data.put(3l, new TypeOfPublicPlaces(3l, "Highway"));
        data.put(4l, new TypeOfPublicPlaces(4l, "Alley"));
    }

    @Override
    public TypeOfPublicPlaces get(Long id) throws NotFoundException {
        if (id == null || !data.containsKey(id))
            throw new NotFoundException();
        return data.get(id);
    }

    @Override
    public TypeOfPublicPlaces reload(TypeOfPublicPlaces entity) throws NotFoundException {
        if (entity == null)
            throw new NotFoundException();
        return get(entity.getId());
    }

    @Override
    public List<TypeOfPublicPlaces> list(int page, int max) {
        return new ArrayList<>(data.values());
    }

    @Override
    public Integer lastPage(int max) {
        return 10;
    }

}
