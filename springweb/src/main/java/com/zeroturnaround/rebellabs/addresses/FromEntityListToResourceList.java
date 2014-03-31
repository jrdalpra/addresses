package com.zeroturnaround.rebellabs.addresses;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.ResourceSupport;

@RequiredArgsConstructor
public class FromEntityListToResourceList<T, R extends ResourceSupport> {

    private final List<T>                 entities;
    private final ResourceAssembler<T, R> assembler;

    public List<R> getResources() {
        List<R> resources = new ArrayList<>(entities.size());
        for (T entity : entities)
            resources.add(assembler.toResource(entity));
        return resources;
    }

}
