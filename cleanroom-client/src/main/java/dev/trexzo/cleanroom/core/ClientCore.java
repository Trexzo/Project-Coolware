package dev.trexzo.cleanroom.core;

import dev.trexzo.cleanroom.core.event.EventBus;
import dev.trexzo.cleanroom.core.module.Module;
import dev.trexzo.cleanroom.core.module.ModuleRegistry;
import dev.trexzo.cleanroom.core.profile.ProfileService;
import dev.trexzo.cleanroom.core.setting.SettingRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ClientCore implements AutoCloseable {
    private final EventBus events = new EventBus();
    private final ModuleRegistry modules = new ModuleRegistry();
    private final SettingRegistry settings = new SettingRegistry();
    private final ProfileService profiles = new ProfileService(modules, settings);
    private final List<AutoCloseable> resources = new ArrayList<>();
    private State state = State.NEW;

    public EventBus events() {
        return events;
    }

    public ModuleRegistry modules() {
        return modules;
    }

    public SettingRegistry settings() {
        return settings;
    }

    public ProfileService profiles() {
        return profiles;
    }

    public synchronized State state() {
        return state;
    }

    public synchronized void start() {
        if (state != State.NEW) {
            throw new IllegalStateException("ClientCore cannot start from state " + state);
        }
        state = State.RUNNING;
    }

    public synchronized <T extends AutoCloseable> T own(T resource) {
        Objects.requireNonNull(resource, "resource");
        if (state == State.CLOSED) {
            throw new IllegalStateException("ClientCore is closed");
        }
        resources.add(resource);
        return resource;
    }

    @Override
    public synchronized void close() {
        if (state == State.CLOSED) {
            return;
        }

        Throwable failure = null;

        List<Module> moduleSnapshot = modules.snapshot();
        for (int i = moduleSnapshot.size() - 1; i >= 0; i--) {
            Module module = moduleSnapshot.get(i);
            if (!module.enabled()) {
                continue;
            }
            try {
                module.setEnabled(false);
            } catch (RuntimeException | Error closeFailure) {
                failure = appendFailure(failure, closeFailure);
            }
        }

        for (int i = resources.size() - 1; i >= 0; i--) {
            try {
                resources.get(i).close();
            } catch (Exception | LinkageError closeFailure) {
                failure = appendFailure(failure, closeFailure);
            }
        }
        resources.clear();
        state = State.CLOSED;

        if (failure != null) {
            if (failure instanceof RuntimeException runtimeFailure) {
                throw runtimeFailure;
            }
            if (failure instanceof Error error) {
                throw error;
            }
            throw new IllegalStateException("ClientCore shutdown failed", failure);
        }
    }

    private static Throwable appendFailure(Throwable first, Throwable next) {
        if (first == null) {
            return next;
        }
        if (next != first) {
            first.addSuppressed(next);
        }
        return first;
    }

    public enum State {
        NEW,
        RUNNING,
        CLOSED
    }
}
