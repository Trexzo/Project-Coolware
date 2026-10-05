package dev.trexzo.cleanroom.core.setting;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class Setting<T> {
    private final String id;
    private final T defaultValue;
    private final Predicate<? super T> validator;
    private final Consumer<? super T> onChange;
    private T value;

    public Setting(String id, T defaultValue, Predicate<? super T> validator, Consumer<? super T> onChange) {
        this.id = requireId(id);
        this.defaultValue = Objects.requireNonNull(defaultValue, "defaultValue");
        this.validator = Objects.requireNonNull(validator, "validator");
        this.onChange = Objects.requireNonNull(onChange, "onChange");
        validate(defaultValue);
        this.value = defaultValue;
    }

    public static <T> Setting<T> of(String id, T defaultValue) {
        return new Setting<>(id, defaultValue, ignored -> true, ignored -> {});
    }

    public String id() {
        return id;
    }

    public T get() {
        return value;
    }

    public T defaultValue() {
        return defaultValue;
    }

    public void set(T requested) {
        Objects.requireNonNull(requested, "requested");
        validate(requested);
        if (Objects.equals(value, requested)) {
            return;
        }
        value = requested;
        onChange.accept(requested);
    }

    public void reset() {
        set(defaultValue);
    }

    private void validate(T candidate) {
        if (!validator.test(candidate)) {
            throw new IllegalArgumentException("Invalid value for setting " + id + ": " + candidate);
        }
    }

    private static String requireId(String id) {
        Objects.requireNonNull(id, "id");
        if (!id.matches("[a-z0-9][a-z0-9._-]*")) {
            throw new IllegalArgumentException("Invalid setting id: " + id);
        }
        return id;
    }
}
