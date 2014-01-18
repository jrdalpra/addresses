package com.zeroturnaround.rebellabs.addresses.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zeroturnaround.rebellabs.addresses.api.exceptions.NotFoundException;
import com.zeroturnaround.rebellabs.addresses.model.Country;
import com.zeroturnaround.rebellabs.addresses.model.State;

public class InMemoryStateRepository implements StatesRepository {

    private Map<Long, State> states = new HashMap<Long, State>();

    {
        states.put(1l, new State(1l, "Santa Catarina", "SC", new Country(1l, null, null)));
        states.put(2l, new State(2l, "Sao Paulo", "SP", new Country(1l, null, null)));
        states.put(3l, new State(3l, "New York", "NY", new Country(2l, null, null)));
    }

    @Override
    public State get(Long id) throws NotFoundException {
        if (!states.containsKey(id))
            throw new NotFoundException();
        return states.get(id);
    }

    @Override
    public List<State> list(int page, int max) {
        return new ArrayList<>(states.values());
    }

    @Override
    public Integer lastPage(int max) {
        return 10;
    }

    @Override
    public List<State> listWhereCountryEquals(Country localized, int page, int max) {
        List<State> fromCountry = new ArrayList<>();
        for (State state : states.values())
            if (state.getCountry().equals(localized))
                fromCountry.add(state);
        return fromCountry;
    }

}
