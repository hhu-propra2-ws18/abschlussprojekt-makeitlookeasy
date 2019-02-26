package de.propra2.ausleiherino24.model;

import java.util.Arrays;
import java.util.List;

public enum Category {
    MOTORS("Motors"),
    BICYCLE("Bicycle"),
    BOAT("Boat"),
    ELECTRONICS("Electronics"),
    BOOKS("Books"),
    MOVIES("Movies"),
    MUSIC("Music"),
    TOOLS("Tools"),
    TOYS("Toys");

    private String name;

    Category(final String name) {
        this.name = name;
    }

    public static List<Category> getAllCategories() {
        return Arrays.asList(values());
    }

    public String getName() {
        return name;
    }

    /**
     * Finds Category matching given value.
     */
    public static Category fromValue(final String value) {
        for (final Category category : values()) {
            if (category.name.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException(
                "Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
    }
}
