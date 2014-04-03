package com.zeroturnaround.rebellabs.addresses.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class State implements Serializable {
    private final static long serialVersionUID = 1L;

    private Long              id;

    private String            name;

    private String            acronym;

    private Country           country;

    public State(Long id) {
        this.id = id;
    }

    public String toString() {
        return id == null ? null : id.toString();
    }

    public static State valueOf(String value) {
        return new State(Long.valueOf(value));
    }
}
