package com.zeroturnaround.rebellabs.addresses.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.zeroturnaround.rebellabs.addresses.api.exceptions.NotFoundException;
import com.zeroturnaround.rebellabs.addresses.model.Country;

public class InMemoryCountriesRepository implements CountriesRepository {

    public final Map<Long, Country> data = new HashMap<>();

    {
        data.put(1l, new Country(1l, "Brasil", "BR"));
        data.put(2l, new Country(2l, "United States of America", "USA"));
    }

    @Override
    public Country get(Long id) throws NotFoundException {
        Country found = data.get(id);
        if (found == null)
            throw new NotFoundException();
        return found;
    }

    @Override
    public List<Country> list(int page, int max) {
        // TODO pagination
        List<Country> countries = new ArrayList<>(data.size());
        for (Entry<Long, Country> entry : data.entrySet())
            if (countries.size() < max)
                countries.add(entry.getValue());
        return countries;
    }

}
