package com.zeroturnaround.rebellabs.addresses.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Country implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long              id;

    private String            name;

    private String            acronym;

    public Country(Long id) {
        this.id = id;
    }

    public String toString() {
        return id == null ? null : id.toString();
    }

    public static Country valueOf(String value) {
        return new Country(Long.valueOf(value));
    }

}
