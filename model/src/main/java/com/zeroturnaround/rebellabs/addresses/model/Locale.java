package com.zeroturnaround.rebellabs.addresses.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Locale implements Serializable {

	public static enum Type {
		CITY, DISTRICT, THORP;
	}

	private static final long serialVersionUID = 1L;

	private final String name;

	private final String acronym;

	private final State state;

	private final Type type;
}
