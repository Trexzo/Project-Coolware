package dev.trexzo.cleanroom.core.module;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ModuleRegistry {
    private final Map<String, Module> modules = new ConcurrentHashMap<>();

    public void register(Module module) {
        Objects.requireNonNull(module, "module");
        Module previous = modules.putIfAbsent(module.id(), module);
        if (previous != null) {
            throw new IllegalStateException("Duplicate module id: " + module.id());
        }
    }

    public Optional<Module> find(String id) {
        return Optional.ofNullable(modules.get(id));
    }

    public <T extends Module> Optional<T> find(Class<T> type) {
        Objects.requireNonNull(type, "type");
        return modules.values().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst();
    }

    public List<Module> snapshot() {
        return modules.values().stream()
                .sorted(Comparator.comparing(Module::id))
                .toList();
    }

    public Collection<Module> byCategory(Module.Category category) {
        Objects.requireNonNull(category, "category");
        return snapshot().stream()
                .filter(module -> module.category() == category)
                .toList();
    }

    public int size() {
        return modules.size();
    }
}
