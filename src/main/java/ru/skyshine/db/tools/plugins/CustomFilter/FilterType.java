package ru.skyshine.db.tools.plugins.CustomFilter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum FilterType {
    CONTAINS("...a... | Содержит"),
    BEGINS("a... | Начинается с"),
    ENDS("...a | Заканчивается на"),
    EQUALS("= | Равно"),
    NOT_EQUAL("!= | Не равно"),
    MORE("> | Больше"),
    MORE_OR_EQUAL(">= | Больше или равно"),
    LESS_OR_EQUAL("<= | Меньше или равно"),
    LESS("< | Меньше");

    private String sign;

    public static FilterType fromStringToEnum(String text) {
        for (FilterType filterType : FilterType.values()) {
            if (filterType.sign.equalsIgnoreCase(text)) {
                return filterType;
            }
        }
        throw new IllegalArgumentException("No constant with sign " + text + " found");
    }

    public static List<String> allStringEnum() {
        List<String> result = new ArrayList<>();
        for (FilterType filterType : FilterType.values()) {
            result.add(filterType.sign);
        }
        return result;
    }
}
