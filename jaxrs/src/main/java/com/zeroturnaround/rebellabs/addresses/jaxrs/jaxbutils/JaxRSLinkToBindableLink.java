package com.zeroturnaround.rebellabs.addresses.jaxrs.jaxbutils;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JaxRSLinkToBindableLink extends XmlAdapter<BindableLink, Link> {

    @Override
    public Link unmarshal(BindableLink link) throws Exception {
        return Link.fromUri(link.getHref()).rel(link.getRel()).build();
    }

    @Override
    public BindableLink marshal(Link link) throws Exception {
        return new BindableLink(link.getUri(), link.getRel());
    }

}
