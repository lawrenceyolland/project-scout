package com.projectscout;

public record Signal<T extends Enum<T>>(
        T signal,
        boolean present,
        Severity severity,
        String description
) {

}
