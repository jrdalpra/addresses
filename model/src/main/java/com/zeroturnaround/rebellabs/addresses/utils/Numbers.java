package com.zeroturnaround.rebellabs.addresses.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Numbers {

    public static <N extends Number> N orWhenNull(N number, N otherwise) {
        return number != null ? number : otherwise;
    }

}
