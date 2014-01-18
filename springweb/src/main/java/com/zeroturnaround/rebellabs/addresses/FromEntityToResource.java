package com.zeroturnaround.rebellabs.addresses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

@RequiredArgsConstructor
public class FromEntityToResource<T> {

    private final List<T> entities;

    public List<Resource<T>> getResources() {
        List<Resource<T>> resources = new ArrayList<>(entities.size());
        for (T entity : entities)
            resources.add(new Resource<T>(entity, getLinksTo(entity)));
        return resources;
    }

    private Iterable<Link> getLinksTo(T entity) {
        return Collections.<Link> emptyList();
    }

}
