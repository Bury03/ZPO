package com.validation.strategy;

import java.lang.reflect.Field;
import java.util.Optional;

import com.validation.annotation.Email;

public class EmailStrategy implements ValidationStrategy {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    @Override
    public Optional<String> validate(Field field, Object value) {
        if (!field.isAnnotationPresent(Email.class) || value == null) {
            return Optional.empty();
        }

        if (!(value instanceof String tekst) || tekst.isBlank()) {
            return Optional.empty();
        }

        Email annotation = field.getAnnotation(Email.class);

        if (!tekst.matches(EMAIL_REGEX)) {
            return Optional.of(String.format("Pole %s: %s", field.getName(), annotation.message()));
        }

        return Optional.empty();
    }
}
