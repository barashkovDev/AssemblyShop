package ru.skyshine.db.tools.plugins.CustomFilter;

import lombok.Data;
import org.springframework.ui.Model;
import ru.skyshine.db.tools.additionalOptions.html.Check;
import ru.skyshine.db.tools.storageClasses.ParamMap;
import ru.skyshine.db.tools.streamAdd.Filter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class FilterTablePlugin {
    private Model model;
    private List<String> listFilterTypes;
    private String filterTypeDefault;
    private ParamMap listFilterColumns;
    private List<String> listShowColumns;
    private String path;

    public FilterTablePlugin(Model model, List<String> listFilterTypes, String filterTypeDefault, ParamMap listFilterColumns, List<String> listShowColumns, String path) {
        this.model = model;

        this.listFilterTypes = listFilterTypes;
        this.filterTypeDefault = filterTypeDefault;
        this.listFilterColumns = listFilterColumns;
        this.listShowColumns = listShowColumns;
        this.path = path;
        setFilterParam();
    }

    public FilterTablePlugin(Model model, ParamMap columns, String path) {
        this(model, FilterType.allStringEnum(), FilterType.CONTAINS.getSign(), columns, columns.getParamName(), path);
    }

    public FilterTablePlugin(Model model, FilterType filterTypeDefault, ParamMap columns, String path) throws Exception {
        this(model, FilterType.allStringEnum(), filterTypeDefault.getSign(), columns, columns.getParamName(), path);
    }

    public FilterTablePlugin(Model model, FilterType filterTypeDefault, ParamMap columns, List<String> listShowColumns, String path) throws Exception {
        this(model, FilterType.allStringEnum(), filterTypeDefault.getSign(), columns, listShowColumns, path);
    }

    public <T> List<T> getFilterData(List<T> data, String filterType, String filterColumn, String valueCompare) {
        if (data == null || data.isEmpty()) return Collections.emptyList();
        if (!checkNoNullFullness(filterType, filterColumn, valueCompare))
            return data;
        try {
            String nameColumn = listFilterColumns.getByParamRef(filterColumn);
            Method method = data.get(0).getClass().getMethod("get" + nameColumn.substring(0, 1).toUpperCase() + nameColumn.substring(1));
            Class<?> classField = method.getReturnType();
            if (classField != Integer.class && classField != String.class && classField != Double.class) {
                return null;
            }
            Object typeValueCompare = returnTypeObject(valueCompare, method.getReturnType());
            if (typeValueCompare == null) {
                return null;
            }
            Filter filter = lambdaByFilterType(FilterType.fromStringToEnum(filterType), classField);
            return data.stream()
                    .filter(value -> {
                        try {
                            return filter.filterCompare(method.invoke(value), typeValueCompare);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean checkNoNullFullness(String... fields) {
        for (String field : fields) {
            if (field == null || field.isEmpty())
                return false;
        }
        return true;
    }

    private Object returnTypeObject(String value, Class<?> clazz) {
        if (String.class.equals(clazz))
            return value;
        Object result;
        try {
            if (Integer.class.equals(clazz))
                result = Integer.parseInt(value);
            else if (Double.class.equals(clazz))
                result = Double.parseDouble(value);
            else
                return null;
            return result;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Filter lambdaByFilterType(FilterType filterType, Class<?> classField) {
        return classField.equals(String.class) ? lambdaForString(filterType) : lambdaForNumeric(filterType, classField);
    }


    private Filter lambdaGeneral(FilterType filterType) {
        if (filterType == FilterType.EQUALS)
            return (value, compareValue) -> value.equals(compareValue);
        return (value, compareValue) -> !value.equals(compareValue);
    }

    private Filter lambdaForString(FilterType filterType) {
        return switch (filterType) {
            case EQUALS, NOT_EQUAL -> lambdaGeneral(filterType);
            case CONTAINS -> (value, compareValue) -> String.valueOf(value).contains(String.valueOf(compareValue));
            case BEGINS -> (value, compareValue) -> String.valueOf(value).startsWith(String.valueOf(compareValue));
            case ENDS -> (value, compareValue) -> String.valueOf(value).endsWith(String.valueOf(compareValue));
            default -> null;
        };
    }

    private Filter lambdaForNumeric(FilterType filterType, Class<?> classField) {
        return switch (filterType) {
            case EQUALS, NOT_EQUAL -> lambdaGeneral(filterType);
            case MORE -> {
                if (Integer.class.equals(classField)) {
                    yield (value, compareValue) -> Integer.parseInt(String.valueOf(value)) > Integer.parseInt(String.valueOf(compareValue));
                } else {
                    yield (value, compareValue) -> Double.parseDouble(String.valueOf(value)) > Double.parseDouble(String.valueOf(compareValue));
                }
            }
            case MORE_OR_EQUAL -> {
                if (Integer.class.equals(classField)) {
                    yield (value, compareValue) -> Integer.parseInt(String.valueOf(value)) >= Integer.parseInt(String.valueOf(compareValue));
                } else {
                    yield (value, compareValue) -> Double.parseDouble(String.valueOf(value)) >= Double.parseDouble(String.valueOf(compareValue));
                }
            }
            case LESS_OR_EQUAL -> {
                if (Integer.class.equals(classField)) {
                    yield (value, compareValue) -> Integer.parseInt(String.valueOf(value)) <= Integer.parseInt(String.valueOf(compareValue));
                } else {
                    yield (value, compareValue) -> Double.parseDouble(String.valueOf(value)) <= Double.parseDouble(String.valueOf(compareValue));
                }
            }
            case LESS -> {
                if (Integer.class.equals(classField)) {
                    yield (value, compareValue) -> Integer.parseInt(String.valueOf(value)) < Integer.parseInt(String.valueOf(compareValue));
                } else {
                    yield (value, compareValue) -> Double.parseDouble(String.valueOf(value)) < Double.parseDouble(String.valueOf(compareValue));
                }
            }
            default -> null;
        };
    }

    private void setFilterParam() {
        model.addAttribute("selectTypeFilter", filterTypeDefault.split(" ")[0]);
        model.addAttribute("listFilterTypes", listFilterTypes);
        model.addAttribute("selectColumnFilter", "Колонки фильтрации");
        model.addAttribute("listFilterColumns", listFilterColumns.getParamName());
        model.addAttribute("options", getChecksByStringArray(listShowColumns));
        model.addAttribute("targetURL", path);
    }

    private List<Check> getChecksByStringArray(List<String> name) {
        List<Check> checks = new ArrayList<>();
        for (String s : name) {
            checks.add(new Check(s));
        }
        return checks;
    }
}
