package com.zeroturnaround.rebellabs.addresses.jaxrs.jaxbutils;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "link")
@XmlType(name = "link")
@XmlAccessorType
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BindableLink {
    @XmlAttribute
    private URI    href;

    @XmlAttribute
    private String rel;
}
