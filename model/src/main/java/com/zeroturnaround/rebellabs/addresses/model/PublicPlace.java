package com.zeroturnaround.rebellabs.addresses.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PublicPlace implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long              id;

    private String            name;

    private TypeOfPublicPlaces type;

    private Locale            locale;

    private Neighborhood      neighborhood;

    public PublicPlace(Long id) {
        this.id = id;
    }

    public String toString() {
        return id == null ? null : id.toString();
    }

    public static PublicPlace valueOf(String value) {
        return new PublicPlace(Long.valueOf(value));
    }

}
