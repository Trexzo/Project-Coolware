package dev.trexzo.cleanroom.core.module;

import java.util.Objects;

public abstract class Module {
    private final String id;
    private final String displayName;
    private final Category category;
    private boolean enabled;

    protected Module(String id, String displayName, Category category) {
        this.id = requireId(id);
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.category = Objects.requireNonNull(category, "category");
    }

    public final String id() {
        return id;
    }

    public final String displayName() {
        return displayName;
    }

    public final Category category() {
        return category;
    }

    public final boolean enabled() {
        return enabled;
    }

    public final void setEnabled(boolean requested) {
        if (enabled == requested) {
            return;
        }
        enabled = requested;
        try {
            if (requested) {
                onEnable();
            } else {
                onDisable();
            }
        } catch (RuntimeException failure) {
            enabled = !requested;
            throw failure;
        }
    }

    public final void toggle() {
        setEnabled(!enabled);
    }

    protected void onEnable() {}

    protected void onDisable() {}

    private static String requireId(String id) {
        Objects.requireNonNull(id, "id");
        if (!id.matches("[a-z0-9][a-z0-9._-]*")) {
            throw new IllegalArgumentException("Invalid module id: " + id);
        }
        return id;
    }

    public enum Category {
        COMBAT,
        MOVEMENT,
        PLAYER,
        WORLD,
        RENDER,
        INTERFACE,
        MISC
    }
}
