package com.zeroturnaround.rebellabs.addresses.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class State implements Serializable {
    private final static long serialVersionUID = 1L;

    private Long              id;

    private String            name;

    private String            acronym;

    @Setter
    @JsonIgnore
    private Country           country;

}
