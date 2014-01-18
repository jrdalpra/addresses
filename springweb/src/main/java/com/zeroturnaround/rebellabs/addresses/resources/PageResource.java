package com.zeroturnaround.rebellabs.addresses.resources;

import java.util.List;

import lombok.Getter;

import org.springframework.hateoas.ResourceSupport;

@Getter
public class PageResource<T> extends ResourceSupport {

    private final List<T> content;

    public PageResource(List<T> content) {
        super();
        this.content = content;
        addLinks();
    }

    public void addLinks() {
    }

}
