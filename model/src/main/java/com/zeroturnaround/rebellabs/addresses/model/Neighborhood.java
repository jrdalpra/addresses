package com.zeroturnaround.rebellabs.addresses.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Neighborhood implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long              id;

    private String            name;

    private Locale            locale;

}
