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
public class Locale implements Serializable {

    public static enum Type {
        CITY,
        DISTRICT,
        VILLAGE;
    }

    private static final long serialVersionUID = 1L;

    private Long              id;

    private String            name;

    private State             state;

    private Type              type;

    public Locale(Long id) {
        this.id = id;
    }

    public String toString() {
        return id == null ? null : id.toString();
    }

    public static Locale valueOf(String value) {
        return new Locale(Long.valueOf(value));
    }
}
