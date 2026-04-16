package com.validation.strategy;

import java.lang.reflect.Field;
import java.util.Optional;

import com.validation.annotation.Size;

public class SizeStrategy implements ValidationStrategy {

    @Override
    public Optional<String> validate(Field field, Object value) {
        if (!field.isAnnotationPresent(Size.class) || value == null) {
            return Optional.empty();
        }

        if (!(value instanceof String tekst)) {
            return Optional.empty();
        }

        Size annotation = field.getAnnotation(Size.class);
        int dlugosc = tekst.length();

        if (dlugosc < annotation.min() || dlugosc > annotation.max()) {
            String message = annotation.message()
                    .replace("{min}", String.valueOf(annotation.min()))
                    .replace("{max}", String.valueOf(annotation.max()));

            return Optional.of(String.format("Pole %s: %s", field.getName(), message));
        }

        return Optional.empty();
    }
}
