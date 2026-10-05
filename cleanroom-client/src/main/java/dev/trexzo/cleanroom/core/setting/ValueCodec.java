package dev.trexzo.cleanroom.core.setting;

import java.util.Locale;
import java.util.Objects;

public interface ValueCodec<T> {
    String encode(T value);

    T decode(String encoded);

    ValueCodec<String> STRING = new ValueCodec<>() {
        @Override
        public String encode(String value) {
            return Objects.requireNonNull(value, "value");
        }

        @Override
        public String decode(String encoded) {
            return Objects.requireNonNull(encoded, "encoded");
        }
    };

    ValueCodec<Boolean> BOOLEAN = new ValueCodec<>() {
        @Override
        public String encode(Boolean value) {
            return Boolean.toString(Objects.requireNonNull(value, "value"));
        }

        @Override
        public Boolean decode(String encoded) {
            String normalized = Objects.requireNonNull(encoded, "encoded").toLowerCase(Locale.ROOT);
            return switch (normalized) {
                case "true" -> true;
                case "false" -> false;
                default -> throw new IllegalArgumentException("Invalid boolean: " + encoded);
            };
        }
    };

    ValueCodec<Integer> INTEGER = new ValueCodec<>() {
        @Override
        public String encode(Integer value) {
            return Integer.toString(Objects.requireNonNull(value, "value"));
        }

        @Override
        public Integer decode(String encoded) {
            try {
                return Integer.valueOf(Objects.requireNonNull(encoded, "encoded"));
            } catch (NumberFormatException failure) {
                throw new IllegalArgumentException("Invalid integer: " + encoded, failure);
            }
        }
    };

    ValueCodec<Double> DOUBLE = new ValueCodec<>() {
        @Override
        public String encode(Double value) {
            double candidate = Objects.requireNonNull(value, "value");
            requireFinite(candidate);
            return Double.toString(candidate);
        }

        @Override
        public Double decode(String encoded) {
            try {
                double candidate = Double.parseDouble(Objects.requireNonNull(encoded, "encoded"));
                requireFinite(candidate);
                return candidate;
            } catch (NumberFormatException failure) {
                throw new IllegalArgumentException("Invalid double: " + encoded, failure);
            }
        }

        private static void requireFinite(double candidate) {
            if (!Double.isFinite(candidate)) {
                throw new IllegalArgumentException("Double must be finite: " + candidate);
            }
        }
    };

    static <E extends Enum<E>> ValueCodec<E> enumCodec(Class<E> enumType) {
        Objects.requireNonNull(enumType, "enumType");
        return new ValueCodec<>() {
            @Override
            public String encode(E value) {
                return Objects.requireNonNull(value, "value").name();
            }

            @Override
            public E decode(String encoded) {
                try {
                    return Enum.valueOf(enumType, Objects.requireNonNull(encoded, "encoded"));
                } catch (IllegalArgumentException failure) {
                    throw new IllegalArgumentException(
                            "Invalid " + enumType.getSimpleName() + " value: " + encoded,
                            failure);
                }
            }
        };
    }
}
