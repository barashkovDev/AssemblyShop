package ru.skyshine.db.tools.streamAdd;

@FunctionalInterface
public interface Filter {
    boolean filterCompare(Object value, Object compareValue);
}
