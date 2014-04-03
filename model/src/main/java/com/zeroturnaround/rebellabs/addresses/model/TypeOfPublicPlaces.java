package com.zeroturnaround.rebellabs.addresses.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TypeOfPublicPlaces implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long              id;

    private String            name;

    public TypeOfPublicPlaces(Long id) {
        super();
        this.id = id;
    }

    public String toString() {
        return id == null ? null : id.toString();
    }

    public static TypeOfPublicPlaces valueOf(String value) {
        return new TypeOfPublicPlaces(Long.valueOf(value));
    }

}
