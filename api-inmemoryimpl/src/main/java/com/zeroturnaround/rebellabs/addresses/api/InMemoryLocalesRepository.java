package com.zeroturnaround.rebellabs.addresses.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.zeroturnaround.rebellabs.addresses.api.exceptions.NotFoundException;
import com.zeroturnaround.rebellabs.addresses.model.Locale;
import com.zeroturnaround.rebellabs.addresses.model.Locale.Type;
import com.zeroturnaround.rebellabs.addresses.model.State;

public class InMemoryLocalesRepository implements LocalesRepository {

    @Inject
    private StatesRepository        states;

    private final Map<Long, Locale> data = new HashMap<>();

    @PostConstruct
    public void setup() {
        Long id = 0l;
        for (State state : states.list(0, 999))
            for (Type type : Locale.Type.values())
                for (long i = 1; i <= 10; i++) {
                    id++;
                    data.put(id, new Locale(id, type.name().toLowerCase() + " " + id, state, type));
                }
    }

    @Override
    public Locale get(Long id) throws NotFoundException {
        if (!data.containsKey(id))
            throw new NotFoundException();
        return data.get(id);
    }

    @Override
    public Locale reload(Locale entity) throws NotFoundException {
        if (entity == null || entity.getId() == null)
            throw new NotFoundException();
        return get(entity.getId());
    }

    @Override
    public List<Locale> list(int page, int max) {
        return new ArrayList<>(data.values());
    }

    @Override
    public Integer lastPage(int max) {
        return 10;
    }

    @Override
    public List<Locale> listByStateAndType(State state, Type type, int page, int max) {
        List<Locale> all = list(page, max);
        List<Locale> byType = new ArrayList<>(all.size());
        System.out.println(all);
        for (Locale locale : all)
            if (locale.getState().equals(state) && locale.getType().equals(type))
                byType.add(locale);
        return byType;
    }

    @Override
    public Integer lastPage(State state, Type type, Integer max) {
        return 10;
    }

}
