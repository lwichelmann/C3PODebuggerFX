package de.soulfoxer.c3podebugger.util;

import javafx.fxml.FXML;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class InjectionChecker {

    private InjectionChecker() {} // utility class, no instances

    public static void assertInjection(Object controller) {
        List<String> missing = new ArrayList<>();

        for (Field field : controller.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(FXML.class)) {
                continue;
            }
            try {
                field.setAccessible(true);
                if (field.get(controller) == null) {
                    missing.add(field.getName());
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Could not read field '" + field.getName() + "'", e);
            }
        }

        if (!missing.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (String name : missing) {
                message.append("'").append(name)
                        .append("' is missing, could not be injected into controller '")
                        .append(controller.getClass().getSimpleName())
                        .append("'\n");
            }
            throw new IllegalStateException(message.toString().trim());
        }
    }
}